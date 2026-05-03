package org.dystoria.tweaks.gui.battle;

import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.gui.battle.BattleOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.dystoria.tweaks.battle.BattlePokemonMemory;

import java.util.Locale;
import java.util.Map;

public final class StatChangeRenderer {
    private StatChangeRenderer () {}

    private static final int TEXT_HEIGHT = 11;
    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;

    public static void render (DrawContext context, BattlePokemonMemory memory, boolean isLeft, int order, boolean isCompact) {
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();

        int startingLeftX = isCompact ? BattleOverlay.HORIZONTAL_INSET + BattleOverlay.TILE_WIDTH - 5 - order * 4 : BattleOverlay.HORIZONTAL_INSET;
        int startingRightX = isCompact ? screenWidth - BattleOverlay.HORIZONTAL_INSET - BattleOverlay.TILE_WIDTH + 4 + order * 4 : screenWidth - BattleOverlay.HORIZONTAL_INSET - 1;
        int startingX = isLeft ? startingLeftX : startingRightX;

        int x = startingX;
        int y = isCompact ? BattleOverlay.VERTICAL_INSET + order * BattleOverlay.COMPACT_TILE_HEIGHT : BattleOverlay.VERTICAL_INSET + BattleOverlay.TILE_HEIGHT - 1;

        int iterations = 0;

        for (Map.Entry<String, Integer> statChange : memory.getStatChanges().entrySet()) {
            if (statChange.getValue() == 0) continue;

            Text stat = Text.translatableWithFallback("gui.battle.dystoria-tweaks.stat." + statChange.getKey(), statChange.getKey().toLowerCase(Locale.ROOT));

            String statString = statChange.getValue() > 0 ? "+" + statChange.getValue() : String.valueOf(statChange.getValue());
            MutableText text = Text.translatable("gui.battle.dystoria-tweaks.stat", stat, statString);

            text.setStyle(Style.EMPTY.withFont(CobblemonResources.INSTANCE.getDEFAULT_LARGE()).withBold(true));

            if (isLeft) {
                x += renderBorderedText(context, x, y, text);
            }
            else {
                int textWidth = TEXT_RENDERER.getWidth(text);
                x -= textWidth + 4;
                renderBorderedText(context, x, y, text);
            }

            if (++iterations % 4 == 0) {
                x = startingX;
                y += TEXT_HEIGHT - 1;
            }
        }
    }

    private static int renderBorderedText (DrawContext context, int x, int y, Text text) {
        int textWidth = TEXT_RENDERER.getWidth(text) + 5;
        context.fill(x, y, x + textWidth, y + TEXT_HEIGHT, 0xFF8D8D8D);
        context.fill(x + 2, y + 2, x + textWidth - 2, y + TEXT_HEIGHT - 2, 0xFF676767);
        context.drawBorder(x, y, textWidth, TEXT_HEIGHT, 0xFF2F2F2F);
        context.drawText(TEXT_RENDERER, text, x + 3, y + 1, -1, false);
        return textWidth - 1;
    }
}
