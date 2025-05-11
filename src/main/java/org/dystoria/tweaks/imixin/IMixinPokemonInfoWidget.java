package org.dystoria.tweaks.imixin;

import java.util.List;
import java.util.Set;

public interface IMixinPokemonInfoWidget {
    Set<String> getSeenShinyRarities ();
    String getCurrentShinyRarity ();
    void setShinyRarity (String rarity);

    List<String> getSeenSkins ();
    int getSkinIndex ();
    void setSkinIndex (int value);
}
