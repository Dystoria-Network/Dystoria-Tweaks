package org.dystoria.tweaks.gui;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class TeraWidget extends ClickableWidget {
    private TeraType teraType;

    public TeraWidget(int x, int y) {
        super(x, y, TeraIcons.LENGTH / 2, TeraIcons.LENGTH / 2, Text.translatable("gui.dystoria-tweaks.tera"));
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        if (teraType == null) return;

        GuiUtilsKt.blitk(
            context.getMatrices(),
            TeraIcons.getTeraIcon(teraType.showdownId()),
            this.getX(), this.getY(),
            this.getHeight(), this.getWidth()
        );

        if (this.isHovered()) {
            char[] characters = this.teraType.showdownId().toCharArray();
            if (characters.length > 0) characters[0] = Character.toUpperCase(characters[0]);
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("tooltip.dystoria-tweaks.tera", new String(characters)), mouseX, mouseY);
        }
    }

    @Override
    protected void appendClickableNarrations (NarrationMessageBuilder builder) {

    }

    public void setPokemon (@Nullable Pokemon pokemon) {
        if (pokemon == null) this.teraType = null;
        else this.teraType = pokemon.getTeraType();
    }
}
