package org.dystoria.tweaks.mixin;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CobblemonStatProvider.class, remap = false)
public abstract class CobblemonStatProviderMixin {
    @Inject(method = "getStatForPokemon", at = @At("HEAD"), cancellable = true)
    private void fixedValues (Pokemon pokemon, Stat stat, CallbackInfoReturnable<Integer> cir) {
        if (stat == Stats.HP && pokemon.getSpecies().getResourceIdentifier().getPath().equals("shemurai")) {
            cir.setReturnValue(1);
        }
    }
}
