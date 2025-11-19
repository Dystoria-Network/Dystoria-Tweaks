package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.render.item.CobblemonBuiltinItemRenderer;
import org.dystoria.tweaks.rendering.DystorianPokemonItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CobblemonClient.class)
public class CobblemonClientMixin {
    @ModifyArg(
        method = "initialize",
        at = @At(
            value = "INVOKE",
            target = "Lcom/cobblemon/mod/common/client/render/item/CobblemonBuiltinItemRendererRegistry;register(Lnet/minecraft/item/Item;Lcom/cobblemon/mod/common/client/render/item/CobblemonBuiltinItemRenderer;)V"
        ),
        index = 1
    )
    private CobblemonBuiltinItemRenderer overrideRenderer (CobblemonBuiltinItemRenderer renderer) {
        return new DystorianPokemonItemRenderer();
    }
}
