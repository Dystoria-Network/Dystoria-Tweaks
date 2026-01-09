package org.dystoria.tweaks.gui.battle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;
import org.dystoria.tweaks.battle.BattlePokemonMemory;

public class PokeballPreviewWidget extends ClickableWidget {
    public static final int WIDTH = 10; // TODO: Get actual sprites and use their dimensions
    public static final int HEIGHT = 10;
    private static final Identifier ALIVE_LEFT = DystoriaTweaksClient.identifier("textures/gui/battle/alive_left.png");
    private static final Identifier ALIVE_RIGHT = DystoriaTweaksClient.identifier("textures/gui/battle/alive_right.png");
    private static final Identifier STATUS_LEFT = DystoriaTweaksClient.identifier("textures/gui/battle/status_left.png");
    private static final Identifier STATUS_RIGHT = DystoriaTweaksClient.identifier("textures/gui/battle/status_right.png");
    private static final Identifier FAINTED_LEFT = DystoriaTweaksClient.identifier("textures/gui/battle/fainted_left.png");
    private static final Identifier FAINTED_RIGHT = DystoriaTweaksClient.identifier("textures/gui/battle/fainted_right.png");

    private final BattlePokemonMemory underlying;
    private final boolean isLeft;
    private final int partyIndex;
    private final int partySize;

    public PokeballPreviewWidget (int partyIndex, int partySize, boolean isLeft, BattlePokemonMemory state) {
        super(0, 0, WIDTH, HEIGHT, Text.literal("Pokemon"));
        this.isLeft = isLeft;
        this.underlying = state;
        this.partyIndex = partyIndex;
        this.partySize = partySize;
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.underlying == null) return;

        Identifier texture;
        if (!this.underlying.getState().isAlive()) texture = this.isLeft ? FAINTED_LEFT : FAINTED_RIGHT;
        else if (this.underlying.getState().status().isPresent()) texture = this.isLeft ? STATUS_LEFT : STATUS_RIGHT;
        else texture = this.isLeft ? ALIVE_LEFT : ALIVE_RIGHT;

        context.drawTexture(texture, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight());

        if (this.isHovered()) {
            Text text;
            if (this.underlying.getFormData().isPresent()) text = Text.literal("pokemon: ").append(this.underlying.getFormData().get().showdownId());
            else text = Text.literal("pokemon: ???");
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, text, mouseX, mouseY);
        }
    }

    public void realignToScreen () {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        this.setX(isLeft ? 0 : screenWidth - PokeballPreviewWidget.WIDTH);
        this.setY((screenHeight / 2) - (this.partySize * HEIGHT / 2) + (this.partyIndex * (HEIGHT + 1)));
    }

    @Override
    protected void appendClickableNarrations (NarrationMessageBuilder builder) {

    }

    @Override
    protected boolean isValidClickButton (int button) {
        return false;
    }
}
