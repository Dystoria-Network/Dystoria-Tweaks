package org.dystoria.tweaks.networking;

import com.provismet.cobblemon.lilycobble.networking.battle.BattleStatePacketS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.dystoria.tweaks.battle.BattleHud;

public class PacketCallbacks {
    public static void register () {
        ServerPlayNetworking.registerGlobalReceiver(BattleStatePacketS2C.ID, BattleHud::receivePacket);
    }
}
