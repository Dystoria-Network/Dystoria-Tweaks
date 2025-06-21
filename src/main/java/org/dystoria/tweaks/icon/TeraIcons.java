package org.dystoria.tweaks.icon;

import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;

public interface TeraIcons {
    int LENGTH = 18;

    static Identifier getTeraIcon (String teraType) {
        return DystoriaTweaksClient.identifier("textures/gui/tera/" + teraType + ".png");
    }
}
