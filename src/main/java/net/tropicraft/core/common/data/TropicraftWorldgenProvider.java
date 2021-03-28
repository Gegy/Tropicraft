package net.tropicraft.core.common.data;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.registry.WorldGenSettingsExport;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TropicraftWorldgenProvider<T, R> implements IDataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger LOGGER = LogManager.getLogger(TropicraftWorldgenProvider.class);

	private static final LazyValue<DynamicRegistries.Impl> DYNAMIC_REGISTRIES = new LazyValue<>(() -> {
		DynamicRegistries.Impl dynamicRegistries = new DynamicRegistries.Impl();
		for (Registry<?> registry : WorldGenRegistries.ROOT_REGISTRIES) {
			copyAllToDynamicRegistry(registry, dynamicRegistries);
		}
		return dynamicRegistries;
	});

	private static <T> void copyAllToDynamicRegistry(Registry<T> from, DynamicRegistries dynamicRegistries) {
		dynamicRegistries.func_230521_a_(from.getRegistryKey()).ifPresent(dynamicRegistry -> {
			copyAllToRegistry(from, dynamicRegistry);
		});
	}

	private static <T> void copyAllToRegistry(Registry<T> from, Registry<T> to) {
		for (Map.Entry<RegistryKey<T>, T> entry : from.getEntries()) {
			Registry.register(to, entry.getKey().getLocation(), entry.getValue());
		}
	}

	private final Path root;
	private final String path;
	private final Registry<T> registry;
	private final EntryGenerator<T, R> entryGenerator;
	private final Codec<Supplier<T>> codec;

	private R result;

	private TropicraftWorldgenProvider(DataGenerator dataGenerator, String path, @Nullable Registry<T> registry, Codec<Supplier<T>> codec, EntryGenerator<T, R> entryGenerator) {
		this.root = dataGenerator.getOutputFolder().resolve("data");
		this.path = path;
		this.registry = registry;
		this.entryGenerator = entryGenerator;
		this.codec = codec;
	}

	public static <R> Supplier<R> addConfiguredFeatures(DataGenerator dataGenerator, EntryGenerator<ConfiguredFeature<?, ?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_feature", WorldGenRegistries.CONFIGURED_FEATURE, ConfiguredFeature.field_236264_b_,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addConfiguredSurfaceBuilders(DataGenerator dataGenerator, EntryGenerator<ConfiguredSurfaceBuilder<?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_surface_builder", WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, ConfiguredSurfaceBuilder.field_244393_b_,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addConfiguredCarvers(DataGenerator dataGenerator, EntryGenerator<ConfiguredCarver<?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_carver", WorldGenRegistries.CONFIGURED_CARVER, ConfiguredCarver.field_244390_b_,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addProcessorLists(DataGenerator dataGenerator, EntryGenerator<StructureProcessorList, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/processor_list", WorldGenRegistries.STRUCTURE_PROCESSOR_LIST, IStructureProcessorType.PROCESSOR_LIST_CODEC,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addTemplatePools(DataGenerator dataGenerator, EntryGenerator<JigsawPattern, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/template_pool", WorldGenRegistries.JIGSAW_POOL, JigsawPattern.field_244392_b_,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addConfiguredStructures(DataGenerator dataGenerator, EntryGenerator<StructureFeature<?, ?>, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/configured_structure_feature", WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, StructureFeature.field_244391_b_,
				entryGenerator
		);
	}

	public static <R> Supplier<R> addBiomes(DataGenerator dataGenerator, EntryGenerator<Biome, R> entryGenerator) {
		return add(
				dataGenerator,
				"worldgen/biome", null, Biome.BIOME_CODEC,
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
	public void act(DirectoryCache cache) {
		DynamicRegistries.Impl dynamicRegistries = DYNAMIC_REGISTRIES.getValue();
		DynamicOps<JsonElement> ops = WorldGenSettingsExport.create(JsonOps.INSTANCE, dynamicRegistries);

		this.result = this.entryGenerator.generate((id, entry) -> {
			Path path = this.root.resolve(id.getNamespace()).resolve(this.path).resolve(id.getPath() + ".json");

			Function<Supplier<T>, DataResult<JsonElement>> function = ops.withEncoder(this.codec);

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

			if (this.registry != null) {
				Registry.register(this.registry, id, entry);
				dynamicRegistries.func_230521_a_(this.registry.getRegistryKey()).ifPresent(dynamicRegistry -> {
					Registry.register(dynamicRegistry, id, entry);
				});
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
		return "Tropicraft Worldgen (" + path + ")";
	}

	public interface EntryGenerator<T, R> {
		R generate(WorldgenDataConsumer<T> consumer);
	}
}
