package org.dystoria.tweaks;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.util.Identifier;
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

	}
}