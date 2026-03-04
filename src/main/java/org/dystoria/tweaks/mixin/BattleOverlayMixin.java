package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.gui.battle.BattleOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.dystoria.tweaks.battle.BattleHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BattleOverlay.class)
public abstract class BattleOverlayMixin extends InGameHud {
    private BattleOverlayMixin (MinecraftClient client) {
        super(client);
    }

    @Inject(method = "drawTile", at = @At("TAIL"))
    private void drawStatChanges (
        DrawContext context,
        float tickDelta,
        ActiveClientBattlePokemon activeBattlePokemon,
        boolean left,
        int rank,
        PokedexEntryProgress dexState,
        boolean hasCommand,
        boolean isHovered,
        boolean isCompact,
        CallbackInfo info
    ) {
        BattleHud.drawStatChanges(context, activeBattlePokemon, left, rank, isCompact);
    }
}
