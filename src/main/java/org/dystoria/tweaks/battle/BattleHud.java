package org.dystoria.tweaks.battle;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattleSide;
import com.provismet.cobblemon.lilycobble.networking.battle.BattlePokemonState;
import com.provismet.cobblemon.lilycobble.networking.battle.BattleSideState;
import com.provismet.cobblemon.lilycobble.networking.battle.BattleStatePacketS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.dystoria.tweaks.gui.battle.PokeballPreviewWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BattleHud {
    private static BattleStatePacketS2C state = null;
    private static final Map<UUID, BattlePokemonMemory> memory = new HashMap<>();
    private static final List<PokeballPreviewWidget> pokeballPreviews = new ArrayList<>();

    public static void receivePacket (BattleStatePacketS2C packet, ServerPlayNetworking.Context context) {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (CobblemonClient.INSTANCE.getBattle() == null) return;
        state = packet;

        for (BattleSideState side : packet.sides()) {
            for (BattlePokemonState pokemon : side.getPokemon()) {
                memory.putIfAbsent(pokemon.uuid(), new BattlePokemonMemory(pokemon));
                memory.get(pokemon.uuid()).setState(pokemon);
            }
        }

        for (ClientBattleSide side : battle.getSides()) {
            for (ActiveClientBattlePokemon pokemon : side.getActiveClientBattlePokemon()) {
                if (pokemon.getBattlePokemon() == null) continue;

                UUID uuid = pokemon.getBattlePokemon().getUuid();
                if (memory.containsKey(uuid)) {
                    memory.get(uuid).setFormData(pokemon.getBattlePokemon().getProperties().asRenderablePokemon().getForm());
                }
            }
        }

        int pokemonCount = packet.sides().stream().mapToInt(side -> side.getPokemon().size()).sum();
        if (pokeballPreviews.size() != pokemonCount) {
            pokeballPreviews.clear();
            packet.sides().forEach(side -> {
                Optional<ClientBattleActor> anyActor = side.actors()
                    .stream()
                    .map(actor -> battle.getParticipatingActor(actor.uuid()))
                    .filter(Objects::nonNull)
                    .findAny();

                if (anyActor.isEmpty()) return; // This should not happen!
                boolean isLeft = anyActor.get().getSide().equals(battle.getSide1());

                List<BattlePokemonState> team = side.getPokemon();
                for (int i = 0; i < team.size(); ++i) {
                    BattlePokemonMemory pokemon = memory.get(team.get(i).uuid());
                    if (pokemon == null) continue; // Should never happen, but just in case
                    pokeballPreviews.add(new PokeballPreviewWidget(i, team.size(), isLeft, pokemon));
                }
            });
        }
    }

    public static void hudCallback (DrawContext context, RenderTickCounter counter) {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (battle == null) {
            memory.clear();
            pokeballPreviews.clear();
            state = null;
            return;
        }

        if (state != null && MinecraftClient.getInstance().player != null && MinecraftClient.isHudEnabled()) {
            int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
            int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int mouseX = (int)(MinecraftClient.getInstance().mouse.getX() * width / MinecraftClient.getInstance().getWindow().getWidth());
            int mouseY = (int)(MinecraftClient.getInstance().mouse.getY() * height / MinecraftClient.getInstance().getWindow().getHeight());
            float tickDelta = counter.getTickDelta(true);

            for (PokeballPreviewWidget widget : pokeballPreviews) {
                widget.realignToScreen();
                widget.render(context, mouseX, mouseY, tickDelta);
            }
        }
    }
}
