package net.tropicraft.core.common.dimension.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.tropicraft.Constants;
import net.tropicraft.core.common.dimension.config.TropicsBuilderConfigs;
import net.tropicraft.core.common.dimension.feature.TropicraftConfiguredFeatures;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;
import net.tropicraft.core.common.dimension.surfacebuilders.TropicraftSurfaceBuilders;
import net.tropicraft.core.common.entity.TropicraftEntities;

public final class TropicraftBiomeBuilder {
	public static final int TROPICS_WATER_COLOR = 0x4eecdf;
	public static final int TROPICS_WATER_FOG_COLOR = 0x041f33;

	private final TropicraftConfiguredFeatures features;

	public TropicraftBiomeBuilder(TropicraftConfiguredFeatures features) {
		this.features = features;
	}

	public Biome createTropicsBiome() {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(TropicraftSurfaceBuilders._TROPICS.configured(TropicsBuilderConfigs.TROPICS_CONFIG.get()));

		DefaultTropicsFeatures.addCarvers(generation);

		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.grapefruitTree.decorated(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.2F, 1))));
		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.orangeTree.decorated(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.2F, 1))));
		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.lemonTree.decorated(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.2F, 1))));
		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.limeTree.decorated(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.2F, 1))));

		DefaultTropicsFeatures.addPalmTrees(generation);

		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.eih.withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.01F, 1))));

		DefaultTropicsFeatures.addTropicsFlowers(generation);
		DefaultTropicsFeatures.addPineapples(generation);

		DefaultBiomeFeatures.addDefaultGrass(generation);
		DefaultBiomeFeatures.withTallGrass(generation);

		MobSpawnInfo.Builder spawns = defaultSpawns();
		spawns.addSpawn(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(TropicraftEntities.FAILGULL.get(), 10, 5, 15));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_BEE.get(), 10, 4, 4));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.COWKTAIL.get(), 10, 4, 4));
		spawns.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TREE_FROG.get(), 4, 4, 4));

		return new Biome.Builder()
				.precipitation(Biome.RainType.RAIN)
				.depth(0.1F).scale(0.1F)
				.temperature(2.0F).downfall(1.5F)
				.category(Biome.Category.PLAINS)
				.withGenerationSettings(generation.build())
				.withMobSpawnSettings(spawns.copy())
				.setEffects(defaultAmbience().build())
				.build();
	}

	private BiomeGenerationSettings.Builder defaultGeneration() {
		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder();

		DefaultBiomeFeatures.withStrongholdAndMineshaft(generation);
		DefaultBiomeFeatures.withCommonOverworldBlocks(generation);
		DefaultBiomeFeatures.withOverworldOres(generation);

		generation.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, TropicraftFeatures.VILLAGE.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
		generation.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, TropicraftFeatures.HOME_TREE.get().withConfiguration(new VillageConfig(Constants.MODID + ":home_tree/starts", 10)));

		// Add dummy volcano structure for /locate, this only adds a structure start that places nothing
		generation.withStructure(TropicraftFeatures.VOLCANO.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
		// Volcano feature to add tile entity to the volcano generation. Checks in each chunk if a volcano is nearby.
		generation.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, TropicraftFeatures.VOLCANO.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));

		return generation;
	}

	private MobSpawnInfo.Builder defaultSpawns() {
		MobSpawnInfo.Builder spawns = new MobSpawnInfo.Builder();

		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.PARROT, 20, 1, 2));
		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.V_MONKEY.get(), 20, 1, 3));
		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.IGUANA.get(), 15, 4, 4));
		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_CREEPER.get(), 4, 1, 2));
		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.EIH.get(), 5, 1, 1));

		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_SKELLY.get(), 8, 2, 4));
		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.TROPI_SPIDER.get(), 8, 2, 2));
		spawns.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(TropicraftEntities.ASHEN.get(), 6, 2, 4));

		return spawns;
	}

	private BiomeAmbience.Builder defaultAmbience() {
		return new BiomeAmbience.Builder()
				.setWaterColor(TROPICS_WATER_COLOR)
				.setWaterFogColor(TROPICS_WATER_FOG_COLOR);
	}
}
