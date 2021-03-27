package net.tropicraft.core.common.data;

import net.minecraft.util.ResourceLocation;

public interface WorldgenDataConsumer<T> {
	T register(ResourceLocation id, T entry);
}
