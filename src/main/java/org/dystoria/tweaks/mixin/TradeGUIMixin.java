package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.client.gui.trade.TradeGUI;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.dystoria.tweaks.icon.ShinyIcons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeGUI.class)
public abstract class TradeGUIMixin extends Screen {
    protected TradeGUIMixin (Text title) {
        super(title);
    }

    @Inject(method = "renderPokemonInfo", at = @At("TAIL"))
    private void addShinierIcons (Pokemon pokemon, boolean isOpposing, DrawContext context, int x, int y, int mouseX, int mouseY, CallbackInfo info) {
        if (pokemon == null) return;

        ShinyIcons.placeExtraShinyIcons(
            pokemon,
            context.getMatrices(),
            (x + (isOpposing ? 213.5 : 71.5)) / 0.5,
            (y + 33.5) / 0.5,
            16,
            !isOpposing
        );
    }
}
