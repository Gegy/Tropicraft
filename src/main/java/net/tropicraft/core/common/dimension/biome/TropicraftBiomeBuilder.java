package net.tropicraft.core.common.dimension.biome;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.tropicraft.Constants;
import net.tropicraft.core.common.dimension.feature.TropicraftConfiguredFeatures;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;
import net.tropicraft.core.common.dimension.surfacebuilders.TropicraftConfiguredSurfaceBuilders;
import net.tropicraft.core.common.entity.TropicraftEntities;

public final class TropicraftBiomeBuilder {
	public static final int TROPICS_WATER_COLOR = 0x4eecdf;
	public static final int TROPICS_WATER_FOG_COLOR = 0x041f33;

	private final TropicraftConfiguredFeatures features;
	private final TropicraftConfiguredSurfaceBuilders surfaces;
	private final DefaultTropicsFeatures defaultFeatures;

	public TropicraftBiomeBuilder(TropicraftConfiguredFeatures features, TropicraftConfiguredSurfaceBuilders surfaces) {
		this.features = features;
		this.surfaces = surfaces;

		// TODO: merge default features with this?
		this.defaultFeatures = new DefaultTropicsFeatures(features);
	}

	public Biome createTropicsBiome() {
		BiomeGenerationSettings.Builder generation = defaultGeneration()
				.surfaceBuilder(this.surfaces.tropics);

		this.defaultFeatures.addCarvers(generation);

		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.grapefruitTree);
		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.orangeTree);
		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.lemonTree);
		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.limeTree);

		this.defaultFeatures.addPalmTrees(generation);

		generation.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, this.features.eih);

		this.defaultFeatures.addTropicsFlowers(generation);
		this.defaultFeatures.addPineapples(generation);

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
				.biomeCategory(Biome.Category.PLAINS)
				.generationSettings(generation.build())
				.mobSpawnSettings(spawns.build())
				.specialEffects(defaultAmbience().build())
				.build();
	}

	private BiomeGenerationSettings.Builder defaultGeneration() {
		BiomeGenerationSettings.Builder generation = new BiomeGenerationSettings.Builder();

		DefaultBiomeFeatures.withStrongholdAndMineshaft(generation);
		DefaultBiomeFeatures.withCommonOverworldBlocks(generation);
		DefaultBiomeFeatures.withOverworldOres(generation);

		generation.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, TropicraftFeatures.VILLAGE.get().configured(IFeatureConfig.NO_FEATURE_CONFIG));
		generation.withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, TropicraftFeatures.HOME_TREE.get().configured(new VillageConfig(Constants.MODID + ":home_tree/starts", 10)));

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
}
