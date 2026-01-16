package org.dystoria.tweaks.gui.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;
import org.jetbrains.annotations.Nullable;

public class MarkCounterWidget extends ClickableWidget {
    private static final Identifier TEXTURE = DystoriaTweaksClient.identifier("textures/gui/mark_counter.png");
    private static final Identifier TEXTURE_STATUS = DystoriaTweaksClient.identifier("textures/gui/mark_counter_status.png");
    private static final int WIDTH = 23;
    private static final int HEIGHT = 7;

    private int marks = 0;
    private boolean hasStatus = false;

    public MarkCounterWidget (int x, int y) {
        super(x, y, WIDTH, HEIGHT, Text.translatable("gui.dystoria-tweaks.mark_counter"));
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.marks == 0) return;

        if (this.hasStatus) {
            int x = this.getX() - 28;
            int y = this.getY() - HEIGHT;
            context.drawTexture(TEXTURE_STATUS, x, y, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
            if (mouseX > x && mouseX < x + WIDTH && mouseY > y && mouseY < y + HEIGHT) {
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("tooltip.dystoria-tweaks.mark_counter", this.marks), mouseX, mouseY);
            }
        }
        else {
            context.drawTexture(TEXTURE, this.getX(), this.getY(), 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
            if (this.isHovered() && !this.hasStatus) {
                context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable("tooltip.dystoria-tweaks.mark_counter", this.marks), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void appendClickableNarrations (NarrationMessageBuilder builder) {

    }

    @Override
    protected boolean isValidClickButton (int button) {
        return false;
    }

    public void setMarks (@Nullable Pokemon pokemon) {
        if (pokemon == null) {
            this.marks = 0;
            this.hasStatus = false;
        }
        else {
            this.marks = pokemon.getMarks().size();
            this.hasStatus = pokemon.getStatus() != null;
        }
    }
}
