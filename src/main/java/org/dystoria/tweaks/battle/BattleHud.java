package org.dystoria.tweaks.battle;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattleSide;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.status.PersistentStatus;
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer;
import com.provismet.cobblemon.lilycobble.networking.battle.BattlePokemonState;
import com.provismet.cobblemon.lilycobble.networking.battle.BattleSideState;
import com.provismet.cobblemon.lilycobble.networking.battle.BattleStatePacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.dystoria.tweaks.gui.battle.PokeballPreviewWidget;
import org.dystoria.tweaks.gui.battle.TeamPreviewWidget;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BattleHud {
    private static BattleStatePacketS2C state = null;
    private static int prevTime = 0;
    private static final Map<UUID, BattlePokemonMemory> memory = new HashMap<>();
    private static final List<TeamPreviewWidget> teamPreviews = List.of(new TeamPreviewWidget(0, 0, true), new TeamPreviewWidget(0, 0, false));

    // Item Translation Key Handling

    /**
     * Arg1 is the user, Arg2 is the item
     */
    private static final Set<String> twoArgItemText = Set.of(
        "cobblemon.battle.item.airballoon",
        "cobblemon.battle.item.recycle",
        "cobblemon.battle.item.harvest",
        "cobblemon.battle.item.frisk",
        "cobblemon.battle.item.bestow",
        "cobblemon.battle.heal.leftovers",
        "cobblemon.battle.heal.item",
        "cobblemon.battle.damage.item"
    );

    public static void receivePacket (BattleStatePacketS2C packet, ClientPlayNetworking.Context context) {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (CobblemonClient.INSTANCE.getBattle() == null || MinecraftClient.getInstance().player == null) return;
        state = packet;

        for (BattleSideState side : packet.sides()) {
            for (BattlePokemonState pokemon : side.getPokemon()) {
                memory.putIfAbsent(pokemon.uuid(), new BattlePokemonMemory(pokemon.uuid()));
                memory.get(pokemon.uuid()).setState(pokemon);
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

    public static void tickLocalBattleInfo () {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (CobblemonClient.INSTANCE.getBattle() == null || MinecraftClient.getInstance().player == null) return;

        for (Map.Entry<UUID, BattlePokemonMemory> entry : memory.entrySet()) {
            entry.getValue().setActive(false);
        }

        for (ClientBattleSide side : battle.getSides()) {
            for (ActiveClientBattlePokemon pokemon : side.getActiveClientBattlePokemon()) {
                if (pokemon.getBattlePokemon() == null) continue;

                BattlePokemonMemory pokemonMemory = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                pokemonMemory.setRenderablePokemon(pokemon.getBattlePokemon().getProperties().asRenderablePokemon());
                pokemonMemory.setActive(true);

                if (pokemon.getBattlePokemon().isHpFlat()) {
                    pokemonMemory.setHealthPercentage(pokemon.getBattlePokemon().getHpValue() / pokemon.getBattlePokemon().getMaxHp());
                }
                else {
                    pokemonMemory.setHealthPercentage(pokemon.getBattlePokemon().getHpValue());
                }

                PersistentStatus status = pokemon.getBattlePokemon().getStatus();
                if (status == null) pokemonMemory.setStatus(null);
                else pokemonMemory.setStatus(status.getShowdownName());

                if (teamPreviews.stream().noneMatch(widget -> widget.hasPartyMember(pokemon.getBattlePokemon().getUuid()))) {
                    boolean isLeft;

                    if (battle.getSpectating()) isLeft = side.equals(battle.getSide2());
                    else isLeft = side.equals(battle.getSide1());

                    TeamPreviewWidget widget = isLeft ? teamPreviews.getFirst() : teamPreviews.getLast();
                    widget.addPartyMember(new PokeballPreviewWidget(isLeft, pokemonMemory));
                }
            }
        }

        // If you're in the battle, then you have full information for your own party.
        if (!battle.getSpectating()) {
            ClientBattleActor myActor = battle.getParticipatingActor(MinecraftClient.getInstance().player.getUuid());
            if (myActor != null) {
                for (Pokemon pokemon : myActor.getPokemon()) {
                    BattlePokemonMemory pokemonMemory = memory.computeIfAbsent(pokemon.getUuid(), BattlePokemonMemory::new);
                    pokemonMemory.setRenderablePokemon(pokemon.asRenderablePokemon());
                    pokemonMemory.setMoves(pokemon.getMoveSet());
                    pokemonMemory.setAbility(pokemon.getAbility().getName());

                    if (pokemon.heldItem().isEmpty()) {
                        pokemonMemory.setItem("");
                    }
                    else if (pokemonMemory.getItem() == null) { // Too many weird edge cases involving items, the packets will always be correct so skip this if its already been done.
                        if (pokemon.heldItem().getTranslationKey().contains("cobblemon")) { // User is holding a known Cobblemon item
                            pokemonMemory.setItem(pokemon.heldItem().getTranslationKey());
                        }
                        else { // This is most likely a Polymer custom item
                            pokemonMemory.setItem(pokemon.heldItem().getName().getString().toLowerCase(Locale.ROOT).replace(" ", ""));
                        }
                    }

                    PersistentStatusContainer status = pokemon.getStatus();
                    if (status == null) pokemonMemory.setStatus(null);
                    else pokemonMemory.setStatus(status.getStatus().getShowdownName());

                    if (!teamPreviews.getFirst().hasPartyMember(pokemon.getUuid())) teamPreviews.getFirst().addPartyMember(new PokeballPreviewWidget(true, pokemonMemory));
                }
            }
        }

        for (Map.Entry<UUID, BattlePokemonMemory> entry : memory.entrySet()) {
            if (!entry.getValue().isActive()) {
                entry.getValue().setTempAbility(null);
                entry.getValue().setTransformed(false);
            }
        }
    }

    public static void messageCallback (List<Text> messages) {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (battle == null) return;

        for (Text message : messages) {
            if (message.getContent() instanceof TranslatableTextContent content) {
                if (content.getKey().contains("cobblemon.battle.used_move") && content.getArgs().length >= 2) {
                    Object userArg = content.getArgs()[0];
                    Object moveArg = content.getArgs()[1];
                    if (userArg instanceof Text userText
                        && userText.getContent() instanceof TranslatableTextContent userContent
                        && moveArg instanceof Text moveText
                        && moveText.getContent() instanceof TranslatableTextContent argContent
                    ) {
                        OwnedPokemon owned = OwnedPokemon.fromTextArg(userArg);
                        ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                        if (pokemon == null || pokemon.getBattlePokemon() == null) return;

                        BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                        mem.useMove(argContent.getKey().replace("cobblemon.move.", ""));
                    }
                }
                else if (content.getKey().contains("cobblemon.battle.ability.") && content.getArgs().length >= 1) {
                    OwnedPokemon owned = OwnedPokemon.fromTextArg(content.getArgs()[0]);

                    if (content.getKey().contains("generic") && content.getArgs().length >= 2) {
                        ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                        if (pokemon != null && pokemon.getBattlePokemon() != null) {
                            BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                            if (mem.getAbility() == null && !mem.isTransformed()) {
                                mem.setAbility(content.getArgs()[1].toString());
                            }
                        }
                    }
                    else if (content.getKey().contains("trace") && content.getArgs().length >= 3) {
                        ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                        if (pokemon != null && pokemon.getBattlePokemon() != null) {
                            BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                            mem.setTempAbility(content.getArgs()[2].toString());
                        }

                        ActiveClientBattlePokemon target = getPokemon(battle, OwnedPokemon.fromTextArg(content.getArgs()[1]));
                        if (target != null && target.getBattlePokemon() != null) {
                            BattlePokemonMemory mem = memory.computeIfAbsent(target.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                            if (mem.getAbility() != null && !mem.isTransformed()) {
                                mem.setAbility(content.getArgs()[2].toString());
                            }
                        }
                    }
                    else if ((content.getKey().contains("replace") || content.getKey().contains("receiver")) && content.getArgs().length >= 2) {
                        ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                        if (pokemon != null && pokemon.getBattlePokemon() != null) {
                            BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                            mem.setTempAbility(content.getArgs()[1].toString());
                        }
                    }
                }
                else if (content.getKey().contains("cobblemon.battle.enditem.") && content.getArgs().length > 0) {
                    OwnedPokemon owned = OwnedPokemon.fromTextArg(content.getArgs()[0]);
                    ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                    if (pokemon != null && pokemon.getBattlePokemon() != null) {
                        BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                        mem.setConsumedItem(true);
                    }
                }
                else if (content.getKey().contains("cobblemon.battle.transform") && content.getArgs().length > 0) {
                    OwnedPokemon owned = OwnedPokemon.fromTextArg(content.getArgs()[0]);
                    ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                    if (pokemon != null && pokemon.getBattlePokemon() != null) {
                        BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                        mem.setTransformed(true);
                    }
                }
                else { // There is nothing consistent for items
                    if (twoArgItemText.contains(content.getKey()) && content.getArgs().length >= 2) {
                        String item;
                        OwnedPokemon owned = OwnedPokemon.fromTextArg(content.getArgs()[0]);

                        if (content.getArgs()[1] instanceof Text itemText) {
                            if (itemText.getContent() instanceof TranslatableTextContent itemContent) {
                                item = itemContent.getKey();
                            }
                            else {
                                item = itemText.getString();
                            }
                        }
                        else item = content.getArgs()[1].toString();

                        ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                        if (pokemon != null && pokemon.getBattlePokemon() != null) {
                            BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                            mem.setItem(item);
                        }
                    }
                    else if (content.getKey().equalsIgnoreCase("cobblemon.battle.damage.rockyhelmet") && content.getArgs().length >= 2) {
                        OwnedPokemon owned = OwnedPokemon.fromTextArg(content.getArgs()[1]);
                        ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                        if (pokemon != null && pokemon.getBattlePokemon() != null) {
                            BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                            mem.setItem(CobblemonItems.ROCKY_HELMET);
                        }
                    }
                    else if (content.getKey().equalsIgnoreCase("cobblemon.battle.damage.lifeorb") && content.getArgs().length >= 1) {
                        OwnedPokemon owned = OwnedPokemon.fromTextArg(content.getArgs()[0]);
                        ActiveClientBattlePokemon pokemon = getPokemon(battle, owned);
                        if (pokemon != null && pokemon.getBattlePokemon() != null) {
                            BattlePokemonMemory mem = memory.computeIfAbsent(pokemon.getBattlePokemon().getUuid(), BattlePokemonMemory::new);
                            mem.setItem(CobblemonItems.LIFE_ORB);
                        }
                    }
                }
            }
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

        if (MinecraftClient.getInstance().player != null && MinecraftClient.isHudEnabled()) {
            int time = MinecraftClient.getInstance().player.age;
            if (time != prevTime) {
                tickLocalBattleInfo();
                prevTime = time;
            }

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

    private static ActiveClientBattlePokemon getPokemon (ClientBattle battle, OwnedPokemon pokemon) {
        return getPokemon(battle, pokemon.pokemon, pokemon.owner);
    }

    private static ActiveClientBattlePokemon getPokemon (ClientBattle battle, String name, String owner) {
        for (ClientBattleSide side : battle.getSides()) {
            for (ActiveClientBattlePokemon pokemon : side.getActiveClientBattlePokemon()) {
                if (pokemon.getBattlePokemon() == null) continue;

                if (pokemon.getBattlePokemon().getDisplayName().getString().equals(name)
                    && pokemon.getActor().getDisplayName().getString().equals(owner)
                ) {
                    return pokemon;
                }
            }
        }
        return null;
    }

    private record OwnedPokemon (String pokemon, String owner) {
        private static OwnedPokemon fromTextArg (Object arg) {
            String pokemon;
            String owner;

            if (arg instanceof Text text) {
                if (text.getContent() instanceof TranslatableTextContent content && content.getArgs().length >= 2) {
                    if (content.getArgs()[0] instanceof Text ownerText) owner = ownerText.getString();
                    else owner = content.getArgs()[0].toString();

                    if (content.getArgs()[1] instanceof Text pokemonText) pokemon = pokemonText.getString();
                    else pokemon = content.getArgs()[1].toString();
                }
                else {
                    pokemon = text.getString();
                    owner = text.getString();
                }
            }
            else {
                pokemon = arg.toString();
                owner = arg.toString();
            }

            return new OwnedPokemon(pokemon, owner);
        }
    }
}
