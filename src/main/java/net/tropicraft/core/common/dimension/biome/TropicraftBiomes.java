package net.tropicraft.core.common.dimension.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.SingleRandomFeature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.tropicraft.Constants;
import net.tropicraft.core.common.data.WorldgenDataConsumer;
import net.tropicraft.core.common.dimension.carver.TropicraftConfiguredCarvers;
import net.tropicraft.core.common.dimension.feature.TropicraftConfiguredFeatures;
import net.tropicraft.core.common.dimension.feature.TropicraftConfiguredStructures;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;
import net.tropicraft.core.common.dimension.surfacebuilders.TropicraftConfiguredSurfaceBuilders;
import net.tropicraft.core.common.entity.TropicraftEntities;

public final class TropicraftBiomes {
	public static final int TROPICS_WATER_COLOR = 0x4eecdf;
	public static final int TROPICS_WATER_FOG_COLOR = 0x041f33;

	public static final RegistryKey<Biome> TROPICS_OCEAN = key("tropics_ocean");
	public static final RegistryKey<Biome> TROPICS = key("tropics");
	public static final RegistryKey<Biome> KELP_FOREST = key("kelp_forest");
	public static final RegistryKey<Biome> RAINFOREST_PLAINS = key("rainforest_plains");
	public static final RegistryKey<Biome> RAINFOREST_HILLS = key("rainforest_hills");
	public static final RegistryKey<Biome> RAINFOREST_MOUNTAINS = key("rainforest_mountains");
	public static final RegistryKey<Biome> RAINFOREST_ISLAND_MOUNTAINS = key("rainforest_island_mountains");
	public static final RegistryKey<Biome> TROPICS_RIVER = key("tropics_river");
	public static final RegistryKey<Biome> TROPICS_BEACH = key("tropics_beach");

	private static RegistryKey<Biome> key(String id) {
		return RegistryKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(Constants.MODID, id));
	}

	public final Biome tropics;
	public final Biome tropicsBeach;
	public final Biome rainforestPlains;
	public final Biome rainforestHills;
	public final Biome rainforestMountains;
	public final Biome rainforestIslandMountains;

	public final Biome tropicsOcean;
	public final Biome kelpForest;

	public final Biome tropicsRiver;

	private final TropicraftConfiguredFeatures features;
	private final TropicraftConfiguredStructures structures;
	private final TropicraftConfiguredCarvers carvers;
	private final TropicraftConfiguredSurfaceBuilders surfaces;

	public TropicraftBiomes(WorldgenDataConsumer<Biome> worldgen, TropicraftConfiguredFeatures features, TropicraftConfiguredStructures structures, TropicraftConfiguredCarvers carvers, TropicraftConfiguredSurfaceBuilders surfaces) {
		this.features = features;
		this.structures = structures;
		this.carvers = carvers;
		this.surfaces = surfaces;

		Register biomes = new Register(worldgen);

		this.tropics = biomes.register(TROPICS, createTropics());
		this.tropicsBeach = biomes.register(TROPICS_BEACH, createTropicsBeach());
		this.rainforestPlains = biomes.register(RAINFOREST_PLAINS, createRainforest(0.25F, 0.1F));
		this.rainforestHills = biomes.register(RAINFOREST_HILLS, createRainforest(0.45F, 0.425F));
		this.rainforestMountains = biomes.register(RAINFOREST_ISLAND_MOUNTAINS, createRainforest(0.8F, 0.8F));
		this.rainforestIslandMountains = biomes.register(RAINFOREST_ISLAND_MOUNTAINS, createRainforest(0.1F, 1.2F));

		this.tropicsOcean = biomes.register(TROPICS_OCEAN, createTropicsOcean());
		this.kelpForest = biomes.register(KELP_FOREST, createKelpForest());

		this.tropicsRiver = biomes.register(TROPICS_RIVER, createTropicsRiver());
	}

	// TODO: how will this work?
	/*public static void addFeatures() {
		for (Biome b : ForgeRegistries.BIOMES.getValues()) {
			if (b.getBiomeCategory() == Biome.Category.BEACH) {
				DefaultTropicsFeatures.addPalmTrees(b);
			} else if (b.getBiomeCategory() == Biome.Category.JUNGLE) {
				DefaultTropicsFeatures.addPineapples(b);
			}
		}
	}*/

	private Biome createTropics() {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(surfaces.tropics);

		carvers.addLand(generation);

		features.addFruitTrees(generation);
		features.addPalmTrees(generation);
		features.addEih(generation);
		features.addTropicsFlowers(generation);
		features.addPineapples(generation);

		DefaultBiomeFeatures.addDefaultGrass(generation);
		DefaultBiomeFeatures.addSavannaGrass(generation);

		MobSpawnInfo.Builder spawns = defaultSpawns();
		spawns.addSpawn(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(TropicraftEntities.FAILGULL.get(), 10, 5, 15));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_BEE.get(), 10, 4, 4));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.COWKTAIL.get(), 10, 4, 4));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TREE_FROG.get(), 4, 4, 4));

		return new Biome.Builder()
				.precipitation(Biome.RainType.RAIN)
				.depth(0.1F).scale(0.1F)
				.temperature(2.0F).downfall(1.5F)
				.biomeCategory(Biome.Category.PLAINS)
				.generationSettings(generation.build())
				.mobSpawnSettings(spawns.build())
				.specialEffects(defaultAmbience().build())
				.build();
	}

	private Biome createTropicsBeach() {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(surfaces.sandy);

		carvers.addUnderwater(generation);

		features.addPalmTrees(generation);
		features.addTropicsFlowers(generation);

		generation.addStructureStart(structures.koaVillage);

		return new Biome.Builder()
				.precipitation(Biome.RainType.RAIN)
				.depth(-0.1F).scale(0.1F)
				.temperature(1.5F).downfall(1.25F)
				.biomeCategory(Biome.Category.BEACH)
				.generationSettings(generation.build())
				.mobSpawnSettings(defaultSpawns().build())
				.specialEffects(defaultAmbience().build())
				.build();
	}

	private Biome createRainforest(float depth, float scale) {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);

		carvers.addLand(generation);

		features.addTropicsGems(generation);
		features.addRainforestTrees(generation);

		// TODO used a dummy config here for 1.15 - fix later
		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.RAINFOREST_FLOWERS.get().withConfiguration(DefaultBiomeFeatures.ROSE_BUSH_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(4))));

		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.COFFEE_BUSH.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(5))));
		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.UNDERGROWTH.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(100))));

		generation.addStructureStart(structures.homeTree);

		DefaultBiomeFeatures.addJungleGrass(generation);
		features.addRainforestPlants(generation);

		MobSpawnInfo.Builder spawns = defaultSpawns();
		spawns.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.OCELOT, 10, 1, 1));
		spawns.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.PARROT, 10, 1, 2));
		spawns.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.TREE_FROG.get(), 25, 2, 5));
		spawns.addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_SPIDER.get(), 30, 1, 1));

		return new Biome.Builder()
				.precipitation(Biome.RainType.RAIN)
				.depth(depth).scale(scale)
				.temperature(1.5F).downfall(2.0F)
				.biomeCategory(Biome.Category.JUNGLE)
				.generationSettings(generation.build())
				.mobSpawnSettings(spawns.build())
				.specialEffects(defaultAmbience().build())
				.build();
	}

	private Biome createTropicsOcean() {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(surfaces.sandy);

		carvers.addUnderwater(generation);

		features.addTropicsMetals(generation);

		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_RANDOM_SELECTOR
				.withConfiguration(new SingleRandomFeature(ImmutableList.of(
						Feature.CORAL_TREE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG),
						Feature.CORAL_CLAW.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG),
						Feature.CORAL_MUSHROOM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG))))
				.withPlacement(Placement.TOP_SOLID_HEIGHTMAP_NOISE_BIASED
						.configure(new TopSolidWithNoiseConfig(20, 400.0D, 0.0D, Heightmap.Type.OCEAN_FLOOR_WG))));

		DefaultBiomeFeatures.addDefaultSeagrass(generation);
		features.addUndergroundSeagrass(generation);

		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEA_PICKLE);
		features.addUndergroundPickles(generation);

		MobSpawnInfo.Builder spawns = defaultSpawns();
		this.addOceanWaterCreatures(spawns);
		spawns.addSpawn(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(TropicraftEntities.FAILGULL.get(), 15, 5, 10));

		return new Biome.Builder()
				.precipitation(Biome.RainType.RAIN)
				.depth(-1.6F).scale(0.4F)
				.temperature(1.5F).downfall(1.25F)
				.biomeCategory(Biome.Category.OCEAN)
				.generationSettings(generation.build())
				.mobSpawnSettings(spawns.build())
				.specialEffects(defaultAmbience().build())
				.build();
	}

	private Biome createKelpForest() {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(surfaces.sandy);

		carvers.addUnderwater(generation);

		// KELP!
		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.KELP_COLD);

		features.addUndergroundSeagrass(generation);
		features.addUndergroundPickles(generation);

		MobSpawnInfo.Builder spawns = defaultSpawns();
		this.addOceanWaterCreatures(spawns);

		return new Biome.Builder()
				.precipitation(Biome.RainType.RAIN)
				.depth(-1.5F).scale(0.3F)
				.temperature(1.5F).downfall(1.25F)
				.biomeCategory(Biome.Category.OCEAN)
				.generationSettings(generation.build())
				.mobSpawnSettings(spawns.build())
				.specialEffects(defaultAmbience().build())
				.build();
	}

	private void addOceanWaterCreatures(MobSpawnInfo.Builder spawns) {
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.MARLIN.get(), 10, 1, 4));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.MAN_O_WAR.get(), 2, 1, 1));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.STARFISH.get(), 4, 1, 4));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.SEA_URCHIN.get(), 4, 1, 4));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.DOLPHIN.get(), 3, 4, 7));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.SEAHORSE.get(), 6, 6, 12));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.SEA_TURTLE.get(), 6, 3, 8));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.TROPICAL_FISH.get(), 20, 4, 8));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.EAGLE_RAY.get(), 6, 1, 1));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.HAMMERHEAD.get(), 2, 1, 1));
	}

	private Biome createTropicsRiver() {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(surfaces.sandy);

		carvers.addLand(generation);

		features.addTropicsFlowers(generation);

		MobSpawnInfo.Builder spawns = defaultSpawns();
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.PIRANHA.get(), 20, 1, 12));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(TropicraftEntities.RIVER_SARDINE.get(), 20, 1, 8));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SQUID, 8, 1, 4));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.COD, 4, 1, 5));
		spawns.addSpawn(EntityClassification.WATER_CREATURE, new MobSpawnInfo.Spawners(EntityType.SALMON, 4, 1, 5));

		return new Biome.Builder()
				.precipitation(Biome.RainType.RAIN)
				.depth(-0.7F).scale(0.05F)
				.temperature(1.5F).downfall(1.25F)
				.biomeCategory(Biome.Category.RIVER)
				.generationSettings(generation.build())
				.mobSpawnSettings(spawns.build())
				.specialEffects(defaultAmbience().build())
				.build();
	}

	private BiomeGenerationSettings.Builder defaultGeneration() {
		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder();

		DefaultBiomeFeatures.addDefaultOverworldLandStructures(generation);
		DefaultBiomeFeatures.addDefaultOres(generation);
		DefaultBiomeFeatures.addDefaultUndergroundVariety(generation);

		generation.addStructureStart(structures.homeTree);
		generation.addStructureStart(structures.koaVillage);

		return generation;
	}

	private MobSpawnInfo.Builder defaultSpawns() {
		MobSpawnInfo.Builder spawns = new MobSpawnInfo.Builder();

		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.PARROT, 20, 1, 2));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.V_MONKEY.get(), 20, 1, 3));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.IGUANA.get(), 15, 4, 4));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_CREEPER.get(), 4, 1, 2));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.EIH.get(), 5, 1, 1));

		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_SKELLY.get(), 8, 2, 4));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_SPIDER.get(), 8, 2, 2));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.ASHEN.get(), 6, 2, 4));

		return spawns;
	}

	private BiomeAmbience.Builder defaultAmbience() {
		return new BiomeAmbience.Builder()
				.waterColor(TROPICS_WATER_COLOR)
				.waterFogColor(TROPICS_WATER_FOG_COLOR);
	}

	static final class Register {
		private final WorldgenDataConsumer<Biome> worldgen;

		Register(WorldgenDataConsumer<Biome> worldgen) {
			this.worldgen = worldgen;
		}

		public Biome register(RegistryKey<Biome> id, Biome biome) {
			return this.worldgen.register(id, biome);
		}
	}
}
