package org.dystoria.tweaks.battle;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattleSide;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.provismet.cobblemon.lilycobble.networking.battle.BattlePokemonState;
import com.provismet.cobblemon.lilycobble.networking.battle.BattleSideState;
import com.provismet.cobblemon.lilycobble.networking.battle.BattleStatePacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.dystoria.tweaks.gui.battle.PokeballPreviewWidget;
import org.dystoria.tweaks.gui.battle.TeamPreviewWidget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BattleHud {
    private static BattleStatePacketS2C state = null;
    private static final Map<UUID, BattlePokemonMemory> memory = new HashMap<>();
    private static final List<TeamPreviewWidget> teamPreviews = List.of(new TeamPreviewWidget(0, 0, true), new TeamPreviewWidget(0, 0, false));

    public static void receivePacket (BattleStatePacketS2C packet, ClientPlayNetworking.Context context) {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (CobblemonClient.INSTANCE.getBattle() == null || MinecraftClient.getInstance().player == null) return;
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

                BattlePokemonMemory pokemonMemory = memory.get(pokemon.getBattlePokemon().getUuid());
                if (pokemonMemory != null) {
                    pokemonMemory.setRenderablePokemon(pokemon.getBattlePokemon().getProperties().asRenderablePokemon());
                }
            }
        }

        if (!battle.getSpectating()) {
            ClientBattleActor myActor = battle.getParticipatingActor(MinecraftClient.getInstance().player.getUuid());
            if (myActor != null) {
                for (Pokemon pokemon : myActor.getPokemon()) { // You always have full information of your own pokemon.
                    BattlePokemonMemory pokemonMemory = memory.get(pokemon.getUuid());
                    if (pokemonMemory != null) {
                        pokemonMemory.setRenderablePokemon(pokemon.asRenderablePokemon());
                        pokemonMemory.setMoves(pokemon.getMoveSet());
                    }
                }
            }
        }

        int pokemonCount = packet.sides().stream().mapToInt(side -> side.getPokemon().size()).sum();
        int widgetCount = teamPreviews.stream().mapToInt(TeamPreviewWidget::getPartySize).sum();
        if (widgetCount != pokemonCount) {
            teamPreviews.forEach(TeamPreviewWidget::clearParty);

            packet.sides().forEach(side -> {
                Optional<ClientBattleActor> anyActor = side.actors()
                    .stream()
                    .map(actor -> battle.getParticipatingActor(actor.uuid()))
                    .filter(Objects::nonNull)
                    .findAny();

                if (anyActor.isEmpty()) return; // This should not happen!
                boolean isLeft = anyActor.get().getSide().equals(battle.getSide1());

                for (BattlePokemonState battlePokemonState : side.getPokemon()) {
                    BattlePokemonMemory pokemon = memory.get(battlePokemonState.uuid());
                    if (pokemon == null) continue; // Should never happen, but just in case

                    TeamPreviewWidget widget = isLeft ? teamPreviews.getFirst() : teamPreviews.getLast();
                    widget.addPartyMember(new PokeballPreviewWidget(isLeft, pokemon));
                }
            });
        }
    }

    public static void hudCallback (DrawContext context, RenderTickCounter counter) {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (battle == null) {
            memory.clear();
            teamPreviews.forEach(TeamPreviewWidget::clearParty);
            state = null;
            return;
        }

        if (state != null && MinecraftClient.getInstance().player != null && MinecraftClient.isHudEnabled()) {
            int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
            int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int mouseX = (int)(MinecraftClient.getInstance().mouse.getX() * width / MinecraftClient.getInstance().getWindow().getWidth());
            int mouseY = (int)(MinecraftClient.getInstance().mouse.getY() * height / MinecraftClient.getInstance().getWindow().getHeight());
            float tickDelta = counter.getTickDelta(true);

            for (TeamPreviewWidget widget : teamPreviews) {
                widget.realignToScreen();
                widget.render(context, mouseX, mouseY, tickDelta);
            }
        }
    }
}
