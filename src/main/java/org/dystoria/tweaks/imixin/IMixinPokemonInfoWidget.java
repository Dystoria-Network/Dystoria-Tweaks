package org.dystoria.tweaks.imixin;

import java.util.List;
import java.util.Set;

public interface IMixinPokemonInfoWidget {
    Set<String> dystoria_tweaks$getSeenShinyRarities ();
    String dystoria_tweaks$getCurrentShinyRarity ();
    void dystoria_tweaks$setShinyRarity (String rarity);

    List<String> dystoria_tweaks$getSeenSkins ();
    int dystoria_tweaks$getSkinIndex ();
    void dystoria_tweaks$setSkinIndex (int value);
}
