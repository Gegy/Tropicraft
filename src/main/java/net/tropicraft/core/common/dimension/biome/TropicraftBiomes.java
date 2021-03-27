package net.tropicraft.core.common.dimension.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.tropicraft.Constants;
import net.tropicraft.core.common.data.WorldgenDataConsumer;
import net.tropicraft.core.common.dimension.carver.TropicraftConfiguredCarvers;
import net.tropicraft.core.common.dimension.feature.TropicraftConfiguredFeatures;
import net.tropicraft.core.common.dimension.feature.TropicraftConfiguredStructures;
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

//	public static final RegistryObject<Biome> TROPICS_OCEAN = register("tropics_ocean", TropicsOceanBiome::new);
//	public static final RegistryObject<Biome> TROPICS = register("tropics", TropicsBiome::new);
//	public static final RegistryObject<Biome> KELP_FOREST = register("kelp_forest", TropicsKelpForestBiome::new);
//	public static final RegistryObject<Biome> RAINFOREST_PLAINS = register("rainforest_plains", () -> new TropicraftRainforestBiome(0.25F, 0.1F));
//	public static final RegistryObject<Biome> RAINFOREST_HILLS = register("rainforest_hills", () -> new TropicraftRainforestBiome(0.45F, 0.425F));
//	public static final RegistryObject<Biome> RAINFOREST_MOUNTAINS = register("rainforest_mountains", () -> new TropicraftRainforestBiome(0.8F, 0.8F));
//	public static final RegistryObject<Biome> RAINFOREST_ISLAND_MOUNTAINS = register("rainforest_island_mountains", () -> new TropicraftRainforestBiome(0.1F, 1.2F));
//	public static final RegistryObject<Biome> TROPICS_RIVER = register("tropics_river", TropicsRiverBiome::new);
//	public static final RegistryObject<Biome> TROPICS_BEACH = register("tropics_beach", TropicsBeachBiome::new);

	public final Biome tropics;

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

		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, features.eih);

		defaultFeatures.addTropicsFlowers(generation);
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
