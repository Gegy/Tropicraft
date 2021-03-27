package net.tropicraft.core.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TropicraftWorldgenProvider<T, R> implements IDataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger LOGGER = LogManager.getLogger(TropicraftWorldgenProvider.class);

	private final Path root;
	private final String namespace;
	private final Registry<T> registry;
	private final EntryGenerator<T, R> entryGenerator;
	private final Codec<Supplier<T>> codec;

	private R result;

	private TropicraftWorldgenProvider(DataGenerator dataGenerator, String path, Registry<T> registry, Codec<Supplier<T>> codec, String namespace, EntryGenerator<T, R> entryGenerator) {
		this.root = dataGenerator.getOutputFolder().resolve("data/" + namespace + "/" + path);

		this.namespace = namespace;
		this.registry = registry;
		this.entryGenerator = entryGenerator;
		this.codec = codec;
	}

	public static <R> Supplier<R> addConfiguredFeatures(
			DataGenerator dataGenerator, String namespace,
			EntryGenerator<ConfiguredFeature<?, ?>, R> entryGenerator
	) {
		TropicraftWorldgenProvider<ConfiguredFeature<?, ?>, R> provider = new TropicraftWorldgenProvider<>(
				dataGenerator,
				"worldgen/configured_feature", WorldGenRegistries.CONFIGURED_FEATURE, ConfiguredFeature.CODEC,
				namespace, entryGenerator
		);
		dataGenerator.addProvider(provider);

		return provider::getResult;
	}

	@Override
	public void run(DirectoryCache cache) {
		this.result = this.entryGenerator.generate((id, entry) -> {
			Registry.register(this.registry, new ResourceLocation(this.namespace, id), entry);

			Path path = this.root.resolve(id + ".json");

			Function<Supplier<T>, DataResult<JsonElement>> function = JsonOps.INSTANCE.withEncoder(this.codec);

			try {
				Optional<JsonElement> serialized = function.apply(() -> entry).result();
				if (serialized.isPresent()) {
					IDataProvider.save(GSON, cache, serialized.get(), path);
				} else {
					LOGGER.error("Couldn't serialize worldgen entry at {}", path);
				}
			} catch (IOException e) {
				LOGGER.error("Couldn't save worldgen entry at {}", path, e);
			}

			return entry;
		});
	}

	public R getResult() {
		return result;
	}

	@Override
	public String getName() {
		return "Tropicraft Worldgen";
	}

	public interface EntryGenerator<T, R> {
		R generate(WorldgenEntryConsumer<T> consumer);
	}
}
