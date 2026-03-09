package org.dystoria.tweaks.gui.pokemon;

import com.cobblemon.mod.common.api.types.tera.TeraType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;
import org.dystoria.tweaks.config.DystoriaTweaksConfig;
import org.jetbrains.annotations.Nullable;

public class TeraWidget extends ClickableWidget {
    private static final int TEXTURE_LENGTH = 22;

    private TeraType teraType;

    public TeraWidget(int x, int y) {
        super(x, y, TEXTURE_LENGTH / 2, TEXTURE_LENGTH / 2, Text.translatable("gui.dystoria-tweaks.tera"));
    }

    private static Identifier getTexture (String teraType) {
        return DystoriaTweaksClient.identifier("textures/gui/tera/" + teraType + ".png");
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        if (!DystoriaTweaksConfig.shouldRenderTeraTypes() || teraType == null) return;

        context.drawTexture(
            getTexture(this.teraType.showdownId()),
            this.getX(), this.getY(),
            0, 0,
            this.getWidth(), this.getHeight(),
            this.getWidth(), this.getHeight()
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

    public static int getSummaryX () {
        return 6;
    }

    public static int getSummaryY () {
        return 32;
    }
}
