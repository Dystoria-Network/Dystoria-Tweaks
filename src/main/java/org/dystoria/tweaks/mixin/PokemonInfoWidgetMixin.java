package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexForm;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.pokedex.ScaledButton;
import com.cobblemon.mod.common.client.gui.pokedex.widgets.PokemonInfoWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.llamalad7.mixinextras.sugar.Local;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.icon.PokedexButtons;
import org.dystoria.tweaks.imixin.IMixinPokemonInfoWidget;
import org.dystoria.tweaks.resources.DystoriaResourceListener;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mixin(PokemonInfoWidget.class)
public abstract class PokemonInfoWidgetMixin extends SoundlessWidget implements IMixinPokemonInfoWidget {
    public PokemonInfoWidgetMixin (int pX, int pY, int pWidth, int pHeight, @NotNull Text component) {
        super(pX, pY, pWidth, pHeight, component);
    }

    @Shadow @Final private ScaledButton shinyButton;
    @Shadow private PokedexEntry currentEntry;

    @Shadow public abstract void updateAspects ();

    @Shadow @Final private int pX;
    @Shadow @Final private int pY;
    @Shadow @Final private static Identifier tooltipBackground;
    @Shadow @Final private static Identifier tooltipEdge;

    @Unique private String shinyRarity = "";
    @Unique private final Set<String> seenShinyRarities = new HashSet<>();

    @Unique private int currentSkinAspectIndex = -1;
    @Unique private List<String> seenSkins = List.of();

    @Unique private final ScaledButton skinButton = new ScaledButton(
        this.pX + 126f, this.pY + 50f,
        20, 20,
        PokedexButtons.SKIN_BUTTON_NO,
        0.5f,
        false,
        buttonWidget -> {
            if (this.seenSkins.isEmpty()) return;

            ++this.currentSkinAspectIndex;
            if (this.currentSkinAspectIndex >= this.seenSkins.size()) {
                this.currentSkinAspectIndex = -1;
            }
            this.updateAspects();
        }
    );

    @Override
    public Set<String> dystoria_tweaks$getSeenShinyRarities () {
        return this.seenShinyRarities;
    }

    @Override
    public String dystoria_tweaks$getCurrentShinyRarity () {
        return this.shinyRarity;
    }

    @Override
    public void dystoria_tweaks$setShinyRarity (String shinyRarity) {
        this.shinyRarity = shinyRarity;
    }

    @Override
    public List<String> dystoria_tweaks$getSeenSkins () {
        return this.seenSkins;
    }

    @Override
    public int dystoria_tweaks$getSkinIndex () {
        return this.currentSkinAspectIndex;
    }

    @Override
    public void dystoria_tweaks$setSkinIndex (int value) {
        this.currentSkinAspectIndex = value;
    }

    @Unique
    private void updateButton (ScaledButton button) {
        button.setX((int)button.getButtonX());
        button.setY((int)button.getButtonY());
    }

    @Unique
    private void renderButtonTooltip (DrawContext context, int x, int y, MutableText text) {
        text.setStyle(text.getStyle().withBold(true).withFont(CobblemonResources.INSTANCE.getDEFAULT_LARGE()));
        int tooltipTextWidth = MinecraftClient.getInstance().textRenderer.getWidth(text);
        int tooltipFullWidth = tooltipTextWidth + 6;

        MatrixStack matrices = context.getMatrices();
        GuiUtilsKt.blitk(matrices, tooltipEdge, x - (tooltipFullWidth / 2) - 1, y + 8, 11, 1);
        GuiUtilsKt.blitk(matrices, tooltipBackground, x - (tooltipFullWidth / 2), y + 8, 11, tooltipFullWidth);
        GuiUtilsKt.blitk(matrices, tooltipEdge, x + (tooltipFullWidth / 2), y + 8, 11, 1);
        RenderHelperKt.drawScaledText(context, CobblemonResources.INSTANCE.getDEFAULT_LARGE(), text, x, y + 9, 1f, 1f, Integer.MAX_VALUE, 0xFFFFFFFF, true, true, null, null);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void addButtons (int pX, int pY, Function1<PokedexForm, Unit> updateForm, CallbackInfo info) {
        this.addWidget(this.skinButton);
    }

    @Inject(method = "updateAspects", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"), remap = false)
    private void applyCustomAspects (CallbackInfo info, @Local(ordinal = 0) Set<String> aspects) {
        if (!this.shinyRarity.isEmpty()) {
            aspects.add(this.shinyRarity);

            if (Objects.equals(this.shinyRarity, "shinier")) {
                this.shinyButton.setResource(PokedexButtons.SHINIER_BUTTON);
            }
            else if (Objects.equals(this.shinyRarity, "shiniest")) {
                this.shinyButton.setResource(PokedexButtons.SHINIEST_BUTTON);
            }
        }

        if (this.currentSkinAspectIndex >= 0) {
            aspects.add(this.seenSkins.get(this.currentSkinAspectIndex));
            this.skinButton.setResource(PokedexButtons.SKIN_BUTTON_YES);
        }
        else {
            this.skinButton.setResource(PokedexButtons.SKIN_BUTTON_NO);
        }
    }

    @Inject(method = "setupButtons", at = @At("TAIL"), remap = false)
    private void setupAllButtons (PokedexEntry pokedexEntry, PokedexForm pokedexForm, CallbackInfo info) {
        this.seenShinyRarities.clear();
        this.shinyRarity = "";

        Set<String> seenAspects = CobblemonClient.INSTANCE.getClientPokedexData().getSeenAspects(pokedexEntry);
        if (seenAspects.contains("shinier")) this.seenShinyRarities.add("shinier");
        if (seenAspects.contains("shiniest")) this.seenShinyRarities.add("shiniest");

        if (!this.seenShinyRarities.isEmpty()) {
            this.shinyButton.active = true;
            this.shinyButton.visible = true;
        }

        this.currentSkinAspectIndex = -1;
        this.seenSkins = seenAspects.stream().filter(DystoriaResourceListener::isSkin).sorted(Comparator.comparing(aspect -> aspect)).toList();
        this.skinButton.setButtonX(this.shinyButton.getButtonX());
        this.skinButton.setButtonY(this.shinyButton.getButtonY() + 12);
        this.updateButton(this.skinButton);
        if (this.seenSkins.isEmpty()) {
            this.skinButton.active = false;
            this.skinButton.visible = false;
        }
        else {
            this.skinButton.active = true;
            this.skinButton.visible = true;
        }
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void renderCustomWidgets (DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.currentEntry == null) return;

        boolean hasKnowledge = CobblemonClient.INSTANCE.getClientPokedexData().getKnowledgeForSpecies(this.currentEntry.getSpeciesId()) != PokedexEntryProgress.NONE;
        if (hasKnowledge) {
            this.skinButton.render(context, mouseX, mouseY, delta);
            if (this.currentSkinAspectIndex >= 0 && this.skinButton.isButtonHovered(mouseX, mouseY)) {
                Text skinTooltip = Text.literal("Skin: " + this.seenSkins.get(this.currentSkinAspectIndex));
                this.renderButtonTooltip(context, mouseX, mouseY, MutableText.of(skinTooltip.getContent()));
            }
        }
    }

    @Inject(method = "shinyButton$lambda$4", at = @At("HEAD"), cancellable = true, remap = false)
    private static void overrideShinyButton (PokemonInfoWidget infoWidget, ButtonWidget buttonWidget, CallbackInfo info) {
        IMixinPokemonInfoWidget mixinWidget = ((IMixinPokemonInfoWidget)(Object)infoWidget);
        boolean seenShinyAndNonShiny = infoWidget.getSeenShinyStates().size() > 1;

        if (!infoWidget.getShiny()) {
            if (seenShinyAndNonShiny) {
                mixinWidget.dystoria_tweaks$setShinyRarity("");
                infoWidget.setShiny(true);
                infoWidget.updateAspects();
            }

            info.cancel();
            return;
        }

        Set<String> seenRarities = mixinWidget.dystoria_tweaks$getSeenShinyRarities();
        String currentRarity = mixinWidget.dystoria_tweaks$getCurrentShinyRarity();

        if (currentRarity.isEmpty()) {
            if (seenRarities.contains("shinier")) mixinWidget.dystoria_tweaks$setShinyRarity("shinier");
            else if (seenRarities.contains("shiniest")) mixinWidget.dystoria_tweaks$setShinyRarity("shiniest");
            else if (seenShinyAndNonShiny) { // Only seen shiny, not shinier/shiniest. Set to non-shiny.
                mixinWidget.dystoria_tweaks$setShinyRarity("");
                infoWidget.setShiny(false);
            }
        }
        else if (Objects.equals(currentRarity, "shinier")) {
            if (seenRarities.contains("shiniest")) {
                mixinWidget.dystoria_tweaks$setShinyRarity("shiniest");
            }
            else if (seenShinyAndNonShiny) {
                mixinWidget.dystoria_tweaks$setShinyRarity("");
                infoWidget.setShiny(false);
            }
            else {
                mixinWidget.dystoria_tweaks$setShinyRarity("");
            }
        }
        else if (seenShinyAndNonShiny) { // Current rarity is shiniest
            mixinWidget.dystoria_tweaks$setShinyRarity("");
            infoWidget.setShiny(false);
        }
        else { // Current rarity is shiniest, but a non-shiny has never been seen
            mixinWidget.dystoria_tweaks$setShinyRarity("");
        }

        infoWidget.updateAspects();
        info.cancel();
    }
}
