package org.dystoria.tweaks;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.battle.BattleHud;
import org.dystoria.tweaks.resources.DystoriaResourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DystoriaTweaksClient implements ClientModInitializer {
	public static final String MODID = "dystoria-tweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier identifier (String path) {
		return Identifier.of(MODID, path);
	}

	@Override
	public void onInitializeClient () {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new DystoriaResourceListener());
		HudRenderCallback.EVENT.register(BattleHud::hudCallback);
	}
}