package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import org.dystoria.tweaks.gui.ShinyIcons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.dystoria.tweaks.gui.pokemon.MarkCounterWidget;
import org.dystoria.tweaks.gui.pokemon.TeraWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Summary.class)
public abstract class SummaryMixin extends Screen {
    protected SummaryMixin (Text title) {
        super(title);
    }

    @Shadow private Pokemon selectedPokemon;

    @Unique private final TeraWidget teraWidget = new TeraWidget(0, 0);
    @Unique private final MarkCounterWidget markWidget = new MarkCounterWidget(0, 0);

    @Inject(method = "init", at = @At("TAIL"))
    private void addWidgets (CallbackInfo info) {
        this.addDrawableChild(this.teraWidget);
        this.addDrawableChild(this.markWidget);
    }

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
        }

        this.teraWidget.setX((super.width - Summary.BASE_WIDTH) / 2 + 6);
        this.teraWidget.setY((super.height - Summary.BASE_HEIGHT) / 2 + 32);
        this.teraWidget.setPokemon(this.selectedPokemon);

        this.markWidget.setX((super.width - Summary.BASE_WIDTH) / 2 + 34);
        this.markWidget.setY((super.height - Summary.BASE_HEIGHT) / 2 + 4);
        this.markWidget.setMarks(this.selectedPokemon);
    }
}
