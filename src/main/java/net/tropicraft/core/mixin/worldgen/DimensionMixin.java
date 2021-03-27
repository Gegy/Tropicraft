package net.tropicraft.core.mixin.worldgen;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.Dimension;
import net.tropicraft.core.common.dimension.TropicraftDimension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;

@Mixin(Dimension.class)
public class DimensionMixin {
	@Shadow @Final private static LinkedHashSet<RegistryKey<Dimension>> BUILTIN_ORDER;

	@Inject(method = "<clinit>", at = @At("RETURN"), remap = false)
	private static void init(CallbackInfo ci) {
		BUILTIN_ORDER.add(TropicraftDimension.DIMENSION);
	}
}
