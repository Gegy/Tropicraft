package net.tropicraft.core.common.data;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
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
	private final String path;
	private final Registry<T> registry;
	private final EntryGenerator<T, R> entryGenerator;
	private final Codec<Supplier<T>> codec;

	private R result;

	private TropicraftWorldgenProvider(DataGenerator dataGenerator, String path, Registry<T> registry, Codec<Supplier<T>> codec, EntryGenerator<T, R> entryGenerator) {
		this.root = dataGenerator.getOutputFolder().resolve("data");
		this.path = path;
		this.registry = registry;
		this.entryGenerator = entryGenerator;
		this.codec = codec;
	}

	public static <R> Supplier<R> addConfiguredFeatures(DataGenerator dataGenerator, EntryGenerator<ConfiguredFeature<?, ?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_feature", WorldGenRegistries.CONFIGURED_FEATURE, ConfiguredFeature.CODEC,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addConfiguredSurfaceBuilders(DataGenerator dataGenerator, EntryGenerator<ConfiguredSurfaceBuilder<?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_surface_builder", WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, ConfiguredSurfaceBuilder.CODEC,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addConfiguredCarvers(DataGenerator dataGenerator, EntryGenerator<ConfiguredCarver<?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_carver", WorldGenRegistries.CONFIGURED_CARVER, ConfiguredCarver.CODEC,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addProcessorLists(DataGenerator dataGenerator, EntryGenerator<StructureProcessorList, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/processor_list", WorldGenRegistries.PROCESSOR_LIST, IStructureProcessorType.LIST_CODEC,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addTemplatePools(DataGenerator dataGenerator, EntryGenerator<JigsawPattern, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/template_pool", WorldGenRegistries.TEMPLATE_POOL, JigsawPattern.CODEC,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addConfiguredStructures(DataGenerator dataGenerator, EntryGenerator<StructureFeature<?, ?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_structure_feature", WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, StructureFeature.CODEC,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addBiomes(DataGenerator dataGenerator, EntryGenerator<Biome, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/biome", WorldGenRegistries.BIOME, Biome.CODEC,
				entryGenerator
		);
	}

	private static <T, R> Supplier<R> add(
			DataGenerator dataGenerator,
			String path, Registry<T> registry, Codec<Supplier<T>> codec,
			EntryGenerator<T, R> entryGenerator
	) {
		TropicraftWorldgenProvider<T, R> provider = new TropicraftWorldgenProvider<>(
				dataGenerator,
				path, registry, codec,
				entryGenerator
		);
		dataGenerator.addProvider(provider);

		return provider::getResult;
	}

	@Override
	public void run(DirectoryCache cache) {
		this.result = this.entryGenerator.generate((id, entry) -> {
			Registry.register(this.registry, id, entry);

			Path path = this.root.resolve(id.getNamespace()).resolve(this.path).resolve(id.getPath() + ".json");

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

	private R getResult() {
		Preconditions.checkNotNull(result, "worldgen data not yet generated");
		return result;
	}

	@Override
	public String getName() {
		return "Tropicraft Worldgen";
	}

	public interface EntryGenerator<T, R> {
		R generate(WorldgenDataConsumer<T> consumer);
	}
}
