package org.dystoria.tweaks.gui;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;

public interface TeraIcons {
    int LENGTH = 22;

    static Identifier getTeraIcon (String teraType) {
        return DystoriaTweaksClient.identifier("textures/gui/tera/" + teraType + ".png");
    }

    static void render (DrawContext context, Pokemon pokemon, double x, double y, int mouseX, int mouseY) {
        Identifier teraType = TeraIcons.getTeraIcon(pokemon.getTeraType().showdownId());

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 3000);
        GuiUtilsKt.blitk(
            context.getMatrices(),
            teraType,
            x, y,
            TeraIcons.LENGTH, TeraIcons.LENGTH,
            0, 0,
            TeraIcons.LENGTH, TeraIcons.LENGTH,
            0,
            1, 1, 1,
            1,
            true,
            0.5f
        );

        if (mouseX * 2f >= x && mouseX * 2f <= x + TeraIcons.LENGTH && mouseY * 2f >= y && mouseY * 2f <= y + TeraIcons.LENGTH) {
            char[] characters = pokemon.getTeraType().showdownId().toCharArray();
            if (characters.length > 0) characters[0] = Character.toUpperCase(characters[0]);
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("tooltip.dystoria-tweaks.tera", new String(characters)), mouseX, mouseY);
        }
        context.getMatrices().pop();
    }
}
