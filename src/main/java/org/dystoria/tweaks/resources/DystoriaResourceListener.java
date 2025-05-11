package org.dystoria.tweaks.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.dystoria.tweaks.DystoriaTweaksClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DystoriaResourceListener implements SimpleSynchronousResourceReloadListener {
    private static final Set<String> SKINS = new HashSet<>();

    public static boolean isSkin (String aspect) {
        return SKINS.contains(aspect);
    }

    @Override
    public Identifier getFabricId () {
        return DystoriaTweaksClient.identifier("skin_listener");
    }

    @Override
    public void reload (ResourceManager manager) {
        SKINS.clear();
        Optional<Resource> skinList = manager.getResource(DystoriaTweaksClient.identifier("skins_aspects.json"));

        if (skinList.isPresent()) {
            try (InputStream stream = skinList.get().getInputStream()) {
                String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(text);

                if (jsonElement instanceof JsonObject jsonObject) {
                    if (jsonObject.has("aspects")) {
                        JsonArray array = jsonObject.getAsJsonArray("aspects");
                        for (JsonElement arrayElement : array) {
                            if (arrayElement instanceof JsonPrimitive primitive) {
                                SKINS.add(primitive.getAsString());
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                DystoriaTweaksClient.LOGGER.error("Failed to read skin aspect list from Dystoria due to error: ", e);
            }
        }

        DystoriaTweaksClient.LOGGER.info("Loaded {} Dystorian skins.", SKINS.size());
    }
}
