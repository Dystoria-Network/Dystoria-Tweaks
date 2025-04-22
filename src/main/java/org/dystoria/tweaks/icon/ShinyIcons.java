package org.dystoria.tweaks.icon;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.client.gui.summary.Summary;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ShinyIcons {
    private static final Identifier SHINIER = Identifier.of("dystoria", "textures/gui/shiny/shinier.png");
    private static final Identifier SHINIEST = Identifier.of("dystoria", "textures/gui/shiny/shiniest.png");

    private static final List<Identifier> STARS = List.of(Summary.Companion.getIconShinyResource(), SHINIER, SHINIEST);

    private static Identifier getStar (int tier) {
        if (tier < 0) return Summary.Companion.getIconShinyResource();
        if (tier >= STARS.size()) return STARS.getLast();
        return STARS.get(tier);
    }

    public static void placeExtraShinyIcons (@NotNull Pokemon pokemon, MatrixStack matrices, double x, double y, int length, boolean leftToRight) {
        if (pokemon.getShiny()) {
            int shinyStars = 0;
            if (pokemon.getAspects().contains("shinier")) shinyStars = 1;
            if (pokemon.getAspects().contains("shiniest")) shinyStars = 2;

            int direction = leftToRight ? 1 : -1;
            for (int i = 1; i <= shinyStars; ++i) {
                GuiUtilsKt.blitk(
                    matrices,
                    ShinyIcons.getStar(i),
                    x + (length + 1) * i * direction, y,
                    length, length,
                    0, 0,
                    length, length,
                    0,
                    1, 1, 1,
                    1,
                    true,
                    0.5f
                );
            }
        }
    }
}
