package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.pc.PCGUI;
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

@Mixin(PCGUI.class)
public abstract class PCGUIMixin extends Screen {
    protected PCGUIMixin (Text title) {
        super(title);
    }

    @Shadow private Pokemon previewPokemon;

    @Inject(method = "render", at = @At("TAIL"))
    private void addExtraIcons (DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.previewPokemon != null) {
            int x = (super.width - 349) / 2;
            int y = (super.height - 205) / 2;

            ShinyIcons.placeExtraShinyIcons(
                this.previewPokemon,
                context.getMatrices(),
                (x + 62.5) / 0.5,
                (y + 28.5) / 0.5,
                16,
                false
            );

            // Tera
            double teraX = (x + 61) / 0.5;
            double teraY = (y + 93.5) / 0.5;
            Identifier teraType = TeraIcons.getTeraIcon(this.previewPokemon.getTeraType().showdownId());
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
                char[] characters = this.previewPokemon.getTeraType().showdownId().toCharArray();
                if (characters.length > 0) characters[0] = Character.toUpperCase(characters[0]);
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("tooltip.dystoria-tweaks.tera", new String(characters)), mouseX, mouseY);
            }
        }
    }
}
