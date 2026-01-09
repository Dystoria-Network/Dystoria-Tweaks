package org.dystoria.tweaks.gui.battle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;

import java.util.ArrayList;
import java.util.List;

public class TeamPreviewWidget extends ClickableWidget {
    public static final int BORDER_HEIGHT = 10;
    public static final int WIDTH = PokeballPreviewWidget.WIDTH;
    private static final Identifier TOP_LEFT_BORDER = DystoriaTweaksClient.identifier("textures/gui/battle/top_left.png");
    private static final Identifier BOTTOM_LEFT_BORDER = DystoriaTweaksClient.identifier("textures/gui/battle/bottom_left.png");
    private static final Identifier TOP_RIGHT_BORDER = DystoriaTweaksClient.identifier("textures/gui/battle/top_right.png");
    private static final Identifier BOTTOM_RIGHT_BORDER = DystoriaTweaksClient.identifier("textures/gui/battle/bottom_right.png");

    private final List<PokeballPreviewWidget> party = new ArrayList<>();
    private final boolean isLeft;

    public TeamPreviewWidget (int x, int y, boolean isLeft) {
        super(x, y, WIDTH, BORDER_HEIGHT * 2, Text.literal(""));
        this.isLeft = isLeft;
    }

    @Override
    protected void renderWidget (DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.party.isEmpty()) return;

        context.drawTexture(isLeft ? TOP_LEFT_BORDER : TOP_RIGHT_BORDER, this.getX(), this.getY(), 0, 0, WIDTH, BORDER_HEIGHT, WIDTH, BORDER_HEIGHT);
        this.party.forEach(widget -> widget.render(context, mouseX, mouseY, delta));
        context.drawTexture(isLeft ? BOTTOM_LEFT_BORDER : BOTTOM_RIGHT_BORDER, this.getX(), this.getY() + this.getHeight() - BORDER_HEIGHT, 0, 0, WIDTH, BORDER_HEIGHT, WIDTH, BORDER_HEIGHT);
    }

    public void realignToScreen () {
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        this.setX(this.isLeft ? 0 : screenWidth - WIDTH);
        this.setY((screenHeight - this.getHeight()) / 2);

        for (int i = 0; i < this.party.size(); ++i) {
            PokeballPreviewWidget widget = this.party.get(i);
            widget.setX(this.getX());
            widget.setY(this.getY() + BORDER_HEIGHT + i * widget.getHeight());
        }
    }

    public void addPartyMember (PokeballPreviewWidget widget) {
        this.party.add(widget);
        this.height += PokeballPreviewWidget.HEIGHT;
    }

    public void clearParty () {
        this.party.clear();
        this.height = BORDER_HEIGHT * 2;
    }

    public int getPartySize () {
        return this.party.size();
    }

    public boolean isLeft () {
        return this.isLeft;
    }

    @Override
    protected void appendClickableNarrations (NarrationMessageBuilder builder) {

    }
}
