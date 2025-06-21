package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.icon.ShinyIcons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.dystoria.tweaks.icon.TeraIcons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Summary.class)
public abstract class SummaryMixin extends Screen {
    protected SummaryMixin (Text title) {
        super(title);
    }

    @Shadow public Pokemon selectedPokemon;

    @Inject(method = "render", at = @At("TAIL"))
    private void addSummaryIcons (DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.selectedPokemon != null) {
            // Shiny Stars
            int x = (super.width - 331) / 2;
            int y = (super.height - 161) / 2;

            ShinyIcons.placeExtraShinyIcons(
                this.selectedPokemon,
                context.getMatrices(),
                (x + 62.5) / 0.5,
                (y + 33.5) / 0.5,
                16,
                false
            );

            // Tera
            double teraX = (x + 61) / 0.5;
            double teraY = (y + 99) / 0.5;
            Identifier teraType = TeraIcons.getTeraIcon(this.selectedPokemon.getTeraType().showdownId());
            GuiUtilsKt.blitk(
                context.getMatrices(),
                teraType,
                teraX, teraY,
                TeraIcons.LENGTH, TeraIcons.LENGTH,
                0, 0,
                TeraIcons.LENGTH, TeraIcons.LENGTH,
                0,
                1, 1, 1,
                1,
                true,
                0.5f
            );

            if (mouseX * 2f >= teraX && mouseX * 2f <= teraX + TeraIcons.LENGTH && mouseY * 2f >= teraY && mouseY * 2f <= teraY + TeraIcons.LENGTH) {
                char[] characters = this.selectedPokemon.getTeraType().showdownId().toCharArray();
                if (characters.length > 0) characters[0] = Character.toUpperCase(characters[0]);
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("tooltip.dystoria-tweaks.tera", new String(characters)), mouseX, mouseY);
            }
        }
    }
}
