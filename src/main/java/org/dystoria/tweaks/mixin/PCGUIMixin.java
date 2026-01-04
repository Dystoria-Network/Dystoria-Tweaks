package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.client.gui.pc.PCGUI;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.dystoria.tweaks.gui.ShinyIcons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.dystoria.tweaks.gui.TeraWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PCGUI.class)
public abstract class PCGUIMixin extends Screen {
    protected PCGUIMixin (Text title) {
        super(title);
    }

    @Shadow
    private Pokemon previewPokemon;

    @Unique
    private final TeraWidget teraWidget = new TeraWidget(0, 0);

    @Inject(method = "init", at = @At("TAIL"))
    private void addEggWidget (CallbackInfo info) {
        this.addDrawableChild(this.teraWidget);
    }

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
            this.teraWidget.setX((super.width - PCGUI.BASE_WIDTH) / 2 + 6);
            this.teraWidget.setY((super.height - PCGUI.BASE_HEIGHT) / 2 + 27);
            this.teraWidget.setPokemon(this.previewPokemon);
        }
    }
}
