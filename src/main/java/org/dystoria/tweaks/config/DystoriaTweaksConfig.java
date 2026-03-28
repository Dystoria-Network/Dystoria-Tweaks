package org.dystoria.tweaks.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.provismet.lilylib.util.json.JsonBuilder;
import com.provismet.lilylib.util.json.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import org.dystoria.tweaks.DystoriaTweaksClient;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public final class DystoriaTweaksConfig {
    private DystoriaTweaksConfig () {}

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("dystoria-tweaks.json");
    private static boolean shiny = true;
    private static boolean tera = true;
    private static boolean teamPreview = true;
    private static boolean statChanges = true;
    private static boolean moveTooltips = true;

    public static boolean shouldRenderShinyRarities () {
        return shiny;
    }

    public static void setShouldRenderShinyRarities (boolean value) {
        shiny = value;
    }

    public static boolean shouldRenderTeraTypes () {
        return tera;
    }

    public static void setShouldRenderTeraTypes (boolean value) {
        tera = value;
    }

    public static boolean shouldRenderBattleHUD () {
        return teamPreview;
    }

    public static void setShouldRenderBattleHUD (boolean value) {
        teamPreview = value;
    }

    public static boolean shouldRenderBattleStatChanges () {
        return statChanges;
    }

    public static void setShouldRenderBattleStatChanges (boolean value) {
        statChanges = value;
    }

    public static boolean shouldRenderMoveTooltips () {
        return moveTooltips;
    }

    public static void setShouldRenderMoveTooltips (boolean value) {
        moveTooltips = value;
    }

    public static void save () {
        JsonObject json = new JsonBuilder()
            .append("show_shiny_rarities", shiny)
            .append("show_tera", tera)
            .append("battle_hud", new JsonBuilder()
                .append("show_team_summary", teamPreview)
                .append("show_stat_changes", statChanges)
                .append("show_move_tooltips", moveTooltips))
            .getJson();

        try (FileWriter writer = new FileWriter(FILE.toFile())) {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(json));
        }
        catch (IOException e) {
            DystoriaTweaksClient.LOGGER.error("Dystoria Tweaks failed to save config due to error: ", e);
        }
    }

    public static void load () {
        if (!FILE.toFile().exists()) {
            save();
        }

        try {
            JsonReader reader = JsonReader.file(FILE.toFile());
            if (reader != null) {
                reader.getBoolean("show_shiny_rarities").ifPresent(val -> shiny = val);
                reader.getBoolean("show_tera").ifPresent(val -> tera = val);
                reader.getObjectAsReader("battle_hud").ifPresent(battleHud -> {
                    battleHud.getBoolean("show_team_summary").ifPresent(val -> teamPreview = val);
                    battleHud.getBoolean("show_stat_changes").ifPresent(val -> statChanges = val);
                    battleHud.getBoolean("show_move_tooltips").ifPresent(val -> moveTooltips = val);
                });
            }
        }
        catch (FileNotFoundException e) {
            DystoriaTweaksClient.LOGGER.error("Dystoria Tweaks failed to locate config file.");
        }
    }
}
