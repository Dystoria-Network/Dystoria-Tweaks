package org.dystoria.tweaks.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LanguageGenerator extends FabricLanguageProvider {
    protected LanguageGenerator (FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations (RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("gui.dystoria-tweaks.tera", "Tera Type");
        translationBuilder.add("tooltip.dystoria-tweaks.tera", "Tera Type: %1$s");

        translationBuilder.add("gui.dystoria-tweaks.mark_counter", "Mark Counter");
        translationBuilder.add("tooltip.dystoria-tweaks.mark_counter", "Marks: %1$s");

        translationBuilder.add("gui.battle.dystoria-tweaks.form", "Form");
        translationBuilder.add("gui.battle.dystoria-tweaks.item", "Item");
        translationBuilder.add("gui.battle.dystoria-tweaks.ability", "Ability");
        translationBuilder.add("gui.battle.dystoria-tweaks.ability.temp", "%1$s (%2$s)");
        translationBuilder.add("gui.battle.dystoria-tweaks.moves", "Moves");
        translationBuilder.add("gui.battle.dystoria-tweaks.move.pp", "%1$s / %2$s");

        translationBuilder.add("gui.battle.dystoria-tweaks.field.empty", "---");
        translationBuilder.add("gui.battle.dystoria-tweaks.field.unknown", "???");

        translationBuilder.add("title.dystoria-tweaks.config", "Dystoria Tweaks");
        translationBuilder.add("category.dystoria-tweaks.general", "General");
        translationBuilder.add("entry.dystoria-tweaks.show_rarity", "Show Shinier/Shiniest Stars");
        translationBuilder.add("entry.dystoria-tweaks.tera", "Show Tera Types");
        translationBuilder.add("category.dystoria-tweaks.battle", "Battle HUD");
        translationBuilder.add("entry.dystoria-tweaks.team_preview", "Show Pokemon Inspector");
        translationBuilder.add("entry.dystoria-tweaks.stat_changes", "Show Stat Changes");
        translationBuilder.add("entry.dystoria-tweaks.move_inspector", "Show Move Inspector");
    }
}
