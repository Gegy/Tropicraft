package net.tropicraft.core.common.data;

public interface WorldgenEntryConsumer<T> {
	T register(String id, T entry);
}
