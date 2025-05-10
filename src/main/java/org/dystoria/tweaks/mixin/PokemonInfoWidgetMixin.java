package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexForm;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.pokedex.ScaledButton;
import com.cobblemon.mod.common.client.gui.pokedex.widgets.PokemonInfoWidget;
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.dystoria.tweaks.icon.ShinyIcons;
import org.dystoria.tweaks.imixin.IMixinPokemonInfoWidget;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Mixin(value = PokemonInfoWidget.class, remap = false)
public abstract class PokemonInfoWidgetMixin extends SoundlessWidget implements IMixinPokemonInfoWidget {
    public PokemonInfoWidgetMixin (int pX, int pY, int pWidth, int pHeight, @NotNull Text component) {
        super(pX, pY, pWidth, pHeight, component);
    }

    @Shadow @Final
    private ScaledButton shinyButton;

    @Unique private String shinyRarity = "";
    @Unique private Set<String> seenShinyRarities = new HashSet<>();

    @Override
    public Set<String> getSeenShinyRarities () {
        return this.seenShinyRarities;
    }

    @Override
    public String getCurrentShinyRarity () {
        return this.shinyRarity;
    }

    @Override
    public void setShinyRarity (String shinyRarity) {
        this.shinyRarity = shinyRarity;
    }

    @Inject(method = "updateAspects", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z"))
    private void applyShinyRarities (CallbackInfo info, @Local(ordinal = 0) Set<String> aspects) {
        if (!this.shinyRarity.isEmpty()) {
            aspects.add(this.shinyRarity);

            if (Objects.equals(this.shinyRarity, "shinier")) {
                this.shinyButton.setResource(ShinyIcons.POKEDEX_SHINIER_BUTTON);
            }
            else if (Objects.equals(this.shinyRarity, "shiniest")) {
                this.shinyButton.setResource(ShinyIcons.POKEDEX_SHINIEST_BUTTON);
            }
        }
    }

    @Inject(method = "setupButtons", at = @At("TAIL"))
    private void readShinyRarities (PokedexEntry pokedexEntry, PokedexForm pokedexForm, CallbackInfo info) {
        Set<String> seenAspects = CobblemonClient.INSTANCE.getClientPokedexData().getSeenAspects(pokedexEntry);
        if (seenAspects.contains("shinier")) seenShinyRarities.add("shinier");
        if (seenAspects.contains("shiniest")) seenShinyRarities.add("shiniest");

        if (!seenAspects.isEmpty()) {
            this.shinyButton.active = true;
            this.shinyButton.visible = true;
        }
    }

    @Inject(method = "shinyButton$lambda$4", at = @At("HEAD"), cancellable = true)
    private static void overrideShinyButton (PokemonInfoWidget infoWidget, ButtonWidget buttonWidget, CallbackInfo info) {
        IMixinPokemonInfoWidget mixinWidget = ((IMixinPokemonInfoWidget)(Object)infoWidget);
        boolean seenShinyAndNonShiny = infoWidget.getSeenShinyStates().size() > 1;

        if (!infoWidget.getShiny()) {
            if (seenShinyAndNonShiny) {
                mixinWidget.setShinyRarity("");
                infoWidget.setShiny(true);
                infoWidget.updateAspects();
            }

            info.cancel();
            return;
        }

        Set<String> seenRarities = mixinWidget.getSeenShinyRarities();
        String currentRarity = mixinWidget.getCurrentShinyRarity();

        if (currentRarity.isEmpty()) {
            if (seenRarities.contains("shinier")) mixinWidget.setShinyRarity("shinier");
            else if (seenRarities.contains("shiniest")) mixinWidget.setShinyRarity("shiniest");
            else if (seenShinyAndNonShiny) { // Only seen shiny, not shinier/shiniest. Set to non-shiny.
                mixinWidget.setShinyRarity("");
                infoWidget.setShiny(false);
            }
        }
        else if (Objects.equals(currentRarity, "shinier")) {
            if (seenRarities.contains("shiniest")) {
                mixinWidget.setShinyRarity("shiniest");
            }
            else if (seenShinyAndNonShiny) {
                mixinWidget.setShinyRarity("");
                infoWidget.setShiny(false);
            }
            else {
                mixinWidget.setShinyRarity("");
            }
        }
        else if (seenShinyAndNonShiny) { // Current rarity is shiniest
            mixinWidget.setShinyRarity("");
            infoWidget.setShiny(false);
        }
        else { // Current rarity is shiniest, but a non-shiny has never been seen
            mixinWidget.setShinyRarity("");
        }

        infoWidget.updateAspects();
        info.cancel();
    }
}
