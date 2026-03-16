package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.client.gui.battle.subscreen.BattleMoveSelection;
import net.minecraft.client.gui.DrawContext;
import org.dystoria.tweaks.config.DystoriaTweaksConfig;
import org.dystoria.tweaks.gui.battle.MovePreviewWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BattleMoveSelection.MoveTile.class)
public abstract class MoveTileMixin {
    @Shadow
    public abstract boolean isHovered(double mouseX, double mouseY);

    @Shadow private MoveTemplate moveTemplate;

    @Unique MovePreviewWidget previewWidget;

    @Inject(method = "render", at = @At("TAIL"))
    private void addPreview (DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.isHovered(mouseX, mouseY) && DystoriaTweaksConfig.shouldRenderMoveTooltips()) {
            if (this.previewWidget == null) this.previewWidget = new MovePreviewWidget(this.moveTemplate);
            this.previewWidget.render(context, mouseX, mouseY, delta);
        }
    }
}
