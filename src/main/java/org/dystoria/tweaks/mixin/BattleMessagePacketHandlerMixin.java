package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.client.net.battle.BattleMessageHandler;
import com.cobblemon.mod.common.net.messages.client.battle.BattleMessagePacket;
import net.minecraft.client.MinecraftClient;
import org.dystoria.tweaks.battle.BattleHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BattleMessageHandler.class, remap = false)
public class BattleMessagePacketHandlerMixin {
    @Inject(method = "handle(Lcom/cobblemon/mod/common/net/messages/client/battle/BattleMessagePacket;Lnet/minecraft/client/MinecraftClient;)V", at = @At("TAIL"))
    private void grabMessage (BattleMessagePacket packet, MinecraftClient client, CallbackInfo info) {
        BattleHud.messageCallback(packet.getMessages());
    }
}
