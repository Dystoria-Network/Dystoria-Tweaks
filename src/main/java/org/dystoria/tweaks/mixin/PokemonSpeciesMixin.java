package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PokemonSpecies.ShowdownSpecies.class, remap = false)
public abstract class PokemonSpeciesMixin {
    @Shadow @Final private String baseSpecies;

    @Inject(method = "getMaxHP", at = @At("HEAD"), cancellable = true)
    private void fixShemurai (CallbackInfoReturnable<Integer> cir) {
        if (this.baseSpecies.contains("shemurai")) {
            cir.setReturnValue(1);
        }
    }
}
