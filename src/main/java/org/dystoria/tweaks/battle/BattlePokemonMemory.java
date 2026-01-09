package org.dystoria.tweaks.battle;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.client.gui.PokemonGuiUtilsKt;
import com.cobblemon.mod.common.client.render.models.blockbench.FloatingState;
import com.cobblemon.mod.common.entity.PoseType;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.RenderablePokemon;
import com.cobblemon.mod.common.util.math.QuaternionUtilsKt;
import com.provismet.cobblemon.lilycobble.networking.battle.BattlePokemonState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class BattlePokemonMemory {
    public static final int RENDER_WIDTH = 50;

    private final List<MoveTemplate> knownMoves;
    private BattlePokemonState state;
    private RenderablePokemon renderablePokemon;

    public BattlePokemonMemory (BattlePokemonState state) {
        this.state = state;
        this.knownMoves = new ArrayList<>();
    }

    public BattlePokemonState getState () {
        return this.state;
    }

    public void setState (BattlePokemonState state) {
        this.state = state;
    }

    public void setRenderablePokemon (RenderablePokemon renderablePokemon) {
        this.renderablePokemon = renderablePokemon;
    }

    public List<MoveTemplate> getKnownMoves () {
        return this.knownMoves;
    }

    public void setMoves (MoveSet moves) {
        this.knownMoves.clear();
        for (Move move : moves) {
            this.knownMoves.add(move.getTemplate());
        }
    }

    public void addMove (MoveTemplate move) {
        if (this.knownMoves.size() < 4 && this.knownMoves.stream().noneMatch(template -> template.getName().equalsIgnoreCase(move.getName()))) {
            this.knownMoves.add(move);
        }
    }

    public void render (DrawContext context, int x, int y, float tickDelta, boolean isLeft) {
        if (this.renderablePokemon == null) {
            GuiUtilsKt.drawText(context, Text.literal("pokemon: ???"), x + 15, y, Colors.WHITE);
            return;
        }

        MutableText text = Text.literal("pokemon: ").append(this.renderablePokemon.getForm().showdownId());
        GuiUtilsKt.drawText(context, text, x + 15, y, Colors.WHITE);

        context.getMatrices().push();
        context.getMatrices().translate(x + 20, y + 10, 0);
        PokemonGuiUtilsKt.drawProfilePokemon(
            this.renderablePokemon,
            context.getMatrices(),
            QuaternionUtilsKt.fromEulerXYZDegrees(new Quaternionf(), new Vector3f(13f, isLeft ? -35f : 35f, 0f)),
            PoseType.PROFILE,
            new FloatingState(),
            tickDelta,
            16f,
            true,
            false,
            1f, 1f, 1f, 1f,
            0f, 0f
        );
        context.getMatrices().pop();
    }
}
