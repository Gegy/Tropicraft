package net.tropicraft.core.common.data;

public interface WorldgenDataConsumer<T> {
	T register(String id, T entry);
}
