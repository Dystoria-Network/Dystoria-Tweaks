package org.dystoria.tweaks.battle;

import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.battle.ClientBattle;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattleSide;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BattleHud {
    private static final char ALIVE = '\u234D';
    private static final char STATUS ='\u234E';
    private static final char FAINTED = '\u234F';

    private static final Identifier ALIVE_ICON = Identifier.of("dystorian-extras", "textures/font/alive.png");
    private static final Identifier STATUS_ICON = Identifier.of("dystorian-extras", "textures/font/status.png");;
    private static final Identifier FAINTED_ICON = Identifier.of("dystorian-extras", "textures/font/dead.png");;

    private static final int ICON_LENGTH = 16;

    public static void hudCallback (DrawContext context, RenderTickCounter counter) {
        ClientBattle battle = CobblemonClient.INSTANCE.getBattle();
        if (battle != null && MinecraftClient.getInstance().player != null) {
            ClientBattleSide left = battle.getSide1().getActors().stream().anyMatch(actor -> actor.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) ? battle.getSide1() : battle.getSide2();
            ClientBattleSide right = left == battle.getSide1() ? battle.getSide2() : battle.getSide1();

            renderSide(context, left, true);
            renderSide(context, right, false);
        }
    }

    private static void renderSide (DrawContext context, ClientBattleSide side, boolean isLeft) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        StringBuilder team = new StringBuilder();

        for (ClientBattleActor actor : side.getActors()) {
            if (actor.getType() != ActorType.PLAYER) continue;
            PlayerEntity player = world.getPlayerByUuid(actor.getUuid());
            if (player == null) continue;

            Scoreboard scoreboard = player.getScoreboard();
            ScoreboardObjective teamScoreboard = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
            if (teamScoreboard == null) continue;

            ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(player, teamScoreboard);
            team.append(ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, teamScoreboard.getNumberFormatOr(StyledNumberFormat.EMPTY)).getString().strip());
        }

        List<Identifier> icons = new ArrayList<>();
        for (char icon : team.toString().toCharArray()) {
            if (icon == ALIVE) icons.add(ALIVE_ICON);
            else if (icon == STATUS) icons.add(STATUS_ICON);
            else if (icon == FAINTED) icons.add(FAINTED_ICON);
        }

        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int x = isLeft ? 2 : MinecraftClient.getInstance().getWindow().getScaledWidth() - ICON_LENGTH - 2;
        for (int i = 0; i < icons.size(); ++i) {
            Identifier icon = icons.get(i);
            int y = (height / 2) - (icons.size() * ICON_LENGTH / 2) + (i * (ICON_LENGTH + 1));

            context.drawTexture(icon, x, y, 0, 0, ICON_LENGTH, ICON_LENGTH, ICON_LENGTH, ICON_LENGTH);
        }
    }
}
