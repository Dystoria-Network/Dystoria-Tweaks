package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.CobblemonNetwork;
import com.cobblemon.mod.common.client.net.pokemon.update.PokemonUpdatePacketHandler;
import com.cobblemon.mod.common.net.PacketRegisterInfo;
import com.cobblemon.mod.common.net.messages.client.pokemon.update.TeraTypeUpdatePacket;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CobblemonNetwork.class)
public class CobblemonNetworkMixin {
    @Inject(method = "generateS2CPacketInfoList", at = @At("RETURN"), remap = false)
    private void generateS2CPacketInfoList (CallbackInfoReturnable<List<PacketRegisterInfo<?>>> cir, @Local List<PacketRegisterInfo<?>> list) {
        list.add(
            new PacketRegisterInfo<>(
                TeraTypeUpdatePacket.Companion.getID(),
                TeraTypeUpdatePacket.Companion::decode,
                new PokemonUpdatePacketHandler<>(),
                null
            )
        );
    }
}
