package org.dystoria.tweaks.networking;

import com.provismet.cobblemon.lilycobble.networking.battle.BattleStatePacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.dystoria.tweaks.battle.BattleHud;

public class PacketCallbacks {
    public static void register () {
        ClientPlayNetworking.registerGlobalReceiver(BattleStatePacketS2C.ID, BattleHud::receivePacket);
    }
}
