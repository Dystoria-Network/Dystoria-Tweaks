package org.dystoria.tweaks.gui.battle;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;

public class MovePreviewWidget extends ClickableWidget {
    public static final int WIDTH = 193;
    public static final int HEIGHT = 68;
    public static final Identifier TEXTURE = DystoriaTweaksClient.identifier("textures/gui/battle/move_preview.png");
    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;
    private final MoveTemplate move;

    public MovePreviewWidget (MoveTemplate move) {
        super(0, 0, WIDTH, HEIGHT, Text.literal("Move"));
        this.move = move;
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        this.realignToScreen();
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100);

        context.drawTexture(TEXTURE, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());

        Text none = Text.literal("-");
        Text power = this.move.getPower() > 0 ? Text.literal(String.valueOf((int)this.move.getPower())) : none;
        Text effectChance = this.move.getEffectChances().length == 0 ? Text.literal("-") : Text.literal(String.valueOf(this.move.getEffectChances()[0].intValue())).append("%");
        Text accuracy = this.move.getAccuracy() > 0 ? Text.literal(String.valueOf((int)this.move.getAccuracy())).append("%") : none;

        int powerWidth = TEXT_RENDERER.getWidth(power);
        int effectWidth = TEXT_RENDERER.getWidth(effectChance);
        int accuracyWidth = TEXT_RENDERER.getWidth(accuracy);

        float scale = 0.75f;
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1);

        int leftTextStart = (int)((this.getX() + 15) / scale);
        int leftNumberStart = leftTextStart + 104;
        int rightTextStart = (int)((this.getX() + 107.5) / scale);
        int rightNumberStart = rightTextStart + 106;

        int row1Y = (int)((this.getY() + 6.5) / scale);
        int row2Y = (int)((this.getY() + 18.5) / scale);

        context.drawText(TEXT_RENDERER, Text.translatable("cobblemon.ui.power"), leftTextStart, row1Y, Colors.WHITE, false);
        context.drawText(TEXT_RENDERER, power, rightNumberStart - powerWidth, row1Y, Colors.WHITE, false);

        context.drawText(TEXT_RENDERER, Text.translatable("cobblemon.ui.effect"), leftTextStart, row2Y, Colors.WHITE, false);
        context.drawText(TEXT_RENDERER, effectChance, leftNumberStart - effectWidth, row2Y, Colors.WHITE, false);

        context.drawText(TEXT_RENDERER, Text.translatable("cobblemon.ui.accuracy"), rightTextStart, row2Y, Colors.WHITE, false);
        context.drawText(TEXT_RENDERER, accuracy, rightNumberStart - accuracyWidth, row2Y, Colors.WHITE, false);

        context.drawTextWrapped(TEXT_RENDERER, this.move.getDescription(), (int)((this.getX() + 6) / scale), (int)((this.getY() + 35) / scale), (int)(182 / scale), Colors.WHITE);
        context.getMatrices().pop();

        context.getMatrices().pop();
    }

    private void realignToScreen () {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        this.setX(TeamPreviewWidget.WIDTH + 2);
        this.setY(screenHeight - HEIGHT - 90);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
