package org.dystoria.tweaks.compatibility.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.dystoria.tweaks.config.DystoriaTweaksConfig;

public class DystoriaTweaksConfigMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory () {
        return DystoriaTweaksConfigMenu::build;
    }

    public static Screen build (Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create();
        builder.setParentScreen(parent);
        builder.setTitle(Text.translatable("title.dystoria-tweaks.config"));
        builder.setSavingRunnable(DystoriaTweaksConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.dystoria-tweaks.general"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.dystoria-tweaks.show_rarity"), DystoriaTweaksConfig.shouldRenderShinyRarities())
            .setDefaultValue(true)
            .setSaveConsumer(DystoriaTweaksConfig::setShouldRenderShinyRarities)
            .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.dystoria-tweaks.tera"), DystoriaTweaksConfig.shouldRenderTeraTypes())
            .setDefaultValue(true)
            .setSaveConsumer(DystoriaTweaksConfig::setShouldRenderTeraTypes)
            .build()
        );

        ConfigCategory battleHud = builder.getOrCreateCategory(Text.translatable("category.dystoria-tweaks.battle"));

        battleHud.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.dystoria-tweaks.team_preview"), DystoriaTweaksConfig.shouldRenderBattleHUD())
            .setDefaultValue(true)
            .setSaveConsumer(DystoriaTweaksConfig::setShouldRenderBattleHUD)
            .build()
        );

        battleHud.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.dystoria-tweaks.stat_changes"), DystoriaTweaksConfig.shouldRenderBattleStatChanges())
            .setDefaultValue(true)
            .setSaveConsumer(DystoriaTweaksConfig::setShouldRenderBattleStatChanges)
            .build()
        );

        battleHud.addEntry(entryBuilder.startBooleanToggle(Text.translatable("entry.dystoria-tweaks.move_inspector"), DystoriaTweaksConfig.shouldRenderMoveTooltips())
            .setDefaultValue(true)
            .setSaveConsumer(DystoriaTweaksConfig::setShouldRenderMoveTooltips)
            .build()
        );

        return builder.build();
    }
}
