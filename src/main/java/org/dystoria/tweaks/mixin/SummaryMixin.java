package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.dystoria.tweaks.icon.ShinyIcons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Summary.class, remap = false)
public abstract class SummaryMixin extends Screen {
    protected SummaryMixin (Text title) {
        super(title);
    }

    @Shadow public Pokemon selectedPokemon;

    @Inject(method = "render", at = @At("TAIL"))
    private void addShinierIcons (DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.selectedPokemon != null) {
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
        }
    }
}
