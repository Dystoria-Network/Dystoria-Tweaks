package org.dystoria.tweaks.battle;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.pokemon.FormData;
import com.provismet.cobblemon.lilycobble.networking.battle.BattlePokemonState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattlePokemonMemory {
    private final List<MoveTemplate> knownMoves;
    private BattlePokemonState state;
    private FormData formData;

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

    public Optional<FormData> getFormData () {
        return Optional.ofNullable(formData);
    }

    public void setFormData (FormData formData) {
        this.formData = formData;
    }

    public List<MoveTemplate> getKnownMoves () {
        return this.knownMoves;
    }

    public void addMove (MoveTemplate move) {
        if (this.knownMoves.size() < 4) this.knownMoves.add(move);
    }
}
