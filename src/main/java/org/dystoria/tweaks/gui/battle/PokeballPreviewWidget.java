package org.dystoria.tweaks.gui.battle;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;
import org.dystoria.tweaks.battle.BattlePokemonMemory;

public class PokeballPreviewWidget extends ClickableWidget {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    public static final int HP_WIDTH = 1;
    public static final int HP_HEIGHT = 13;

    private static final Identifier ALIVE_LEFT = DystoriaTweaksClient.identifier("textures/gui/battle/alive_left.png");
    private static final Identifier ALIVE_LEFT_HOVERED = DystoriaTweaksClient.identifier("textures/gui/battle/alive_left_hovered.png");
    private static final Identifier ALIVE_RIGHT = DystoriaTweaksClient.identifier("textures/gui/battle/alive_right.png");
    private static final Identifier ALIVE_RIGHT_HOVERED = DystoriaTweaksClient.identifier("textures/gui/battle/alive_right_hovered.png");
    private static final Identifier ALIVE_HP = DystoriaTweaksClient.identifier("textures/gui/battle/alive_health_bar.png");

    private static final Identifier STATUS_LEFT = DystoriaTweaksClient.identifier("textures/gui/battle/status_left.png");
    private static final Identifier STATUS_LEFT_HOVERED = DystoriaTweaksClient.identifier("textures/gui/battle/status_left_hovered.png");
    private static final Identifier STATUS_RIGHT = DystoriaTweaksClient.identifier("textures/gui/battle/status_right.png");
    private static final Identifier STATUS_RIGHT_HOVERED = DystoriaTweaksClient.identifier("textures/gui/battle/status_right_hovered.png");
    private static final Identifier STATUS_HP = DystoriaTweaksClient.identifier("textures/gui/battle/status_health_bar.png");

    private static final Identifier FAINTED_LEFT = DystoriaTweaksClient.identifier("textures/gui/battle/fainted_left.png");
    private static final Identifier FAINTED_LEFT_HOVERED = DystoriaTweaksClient.identifier("textures/gui/battle/fainted_left_hovered.png");
    private static final Identifier FAINTED_RIGHT = DystoriaTweaksClient.identifier("textures/gui/battle/fainted_right.png");
    private static final Identifier FAINTED_RIGHT_HOVERED = DystoriaTweaksClient.identifier("textures/gui/battle/fainted_right_hovered.png");

    private final BattlePokemonMemory underlying;
    private final boolean isLeft;

    public PokeballPreviewWidget (boolean isLeft, BattlePokemonMemory state) {
        super(0, 0, WIDTH, HEIGHT, Text.literal("Pokemon"));
        this.isLeft = isLeft;
        this.underlying = state;
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.underlying == null || this.underlying.getState() == null) return;

        Identifier texture;
        if (this.isHovered()) {
            if (!this.underlying.getState().isAlive()) texture = this.isLeft ? FAINTED_LEFT_HOVERED : FAINTED_RIGHT_HOVERED;
            else if (this.underlying.getState().status().isPresent()) texture = this.isLeft ? STATUS_LEFT_HOVERED : STATUS_RIGHT_HOVERED;
            else texture = this.isLeft ? ALIVE_LEFT_HOVERED : ALIVE_RIGHT_HOVERED;

            this.underlying.render(context, this.isLeft ? this.getX() + WIDTH : this.getX() - WIDTH - BattlePokemonMemory.RENDER_WIDTH, this.getY(), delta, this.isLeft);
        }
        else {
            if (!this.underlying.getState().isAlive()) texture = this.isLeft ? FAINTED_LEFT : FAINTED_RIGHT;
            else if (this.underlying.getState().status().isPresent()) texture = this.isLeft ? STATUS_LEFT : STATUS_RIGHT;
            else texture = this.isLeft ? ALIVE_LEFT : ALIVE_RIGHT;
        }
        context.drawTexture(texture, this.getX(), this.getY(), 0, 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());

        if (this.underlying.getState().isAlive()) {
            Identifier hpTexture = this.underlying.getState().status().isPresent() ? STATUS_HP : ALIVE_HP;

            int hpX = this.isLeft ? this.getX() + 1 : this.getX() + WIDTH - 2;
            int hpHeight = (int)(this.underlying.getState().healthPercentage() * HP_HEIGHT);
            context.drawTexture(
                hpTexture,
                hpX, this.getY() + 4 + (HP_HEIGHT - hpHeight), 1,
                0, 1f - (float)this.underlying.getState().healthPercentage(),
                HP_WIDTH, hpHeight,
                HP_WIDTH, hpHeight
            );
        }
    }

    @Override
    protected void appendClickableNarrations (NarrationMessageBuilder builder) {

    }

    @Override
    protected boolean isValidClickButton (int button) {
        return false;
    }
}
