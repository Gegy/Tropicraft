package net.tropicraft.core.common.dimension.biome;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockWithContextConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.dimension.feature.TropicraftConfiguredFeatures;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;

public class DefaultTropicsFeatures {

	private final TropicraftConfiguredFeatures features;

	public static final BlockClusterFeatureConfig IRIS_CONFIG = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(TropicraftBlocks.IRIS.get().defaultBlockState()), new DoublePlantBlockPlacer()).tries(64).noProjection().build();

	public DefaultTropicsFeatures(TropicraftConfiguredFeatures features) {
		this.features = features;
	}

	public void addUndergroundSeagrass(BiomeGenerationSettings.Builder biome) {
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(Blocks.SEAGRASS.defaultBlockState(), new BlockState[] { Blocks.STONE.defaultBlockState() }, new BlockState[] { Blocks.WATER.defaultBlockState() }, new BlockState[] { Blocks.WATER.defaultBlockState() })).decorated(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.1F))));
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(Blocks.SEAGRASS.defaultBlockState(), new BlockState[] { Blocks.DIRT.defaultBlockState() }, new BlockState[] { Blocks.WATER.defaultBlockState() }, new BlockState[] { Blocks.WATER.defaultBlockState() })).decorated(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.5F))));
	}

	public void addUndergroundPickles(BiomeGenerationSettings.Builder biome) {
		// TODO maybe combine these into a single feature type that chooses pickle count randomly?
		addPickleFeature(biome, 1, Blocks.STONE.defaultBlockState(), 0.025F);
		addPickleFeature(biome, 2, Blocks.STONE.defaultBlockState(), 0.01F);
		addPickleFeature(biome, 3, Blocks.STONE.defaultBlockState(), 0.005F);
		addPickleFeature(biome, 4, Blocks.STONE.defaultBlockState(), 0.001F);
		addPickleFeature(biome, 1, Blocks.DIRT.defaultBlockState(), 0.05F);
		addPickleFeature(biome, 2, Blocks.DIRT.defaultBlockState(), 0.04F);
		addPickleFeature(biome, 3, Blocks.DIRT.defaultBlockState(), 0.02F);
		addPickleFeature(biome, 4, Blocks.DIRT.defaultBlockState(), 0.01F);
	}

	private void addPickleFeature(BiomeGenerationSettings.Builder biome, int pickles, BlockState placeOn, float chance) {
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(Blocks.SEA_PICKLE.defaultBlockState().with(SeaPickleBlock.PICKLES, pickles), new BlockState[] { placeOn }, new BlockState[] { Blocks.WATER.defaultBlockState() }, new BlockState[] { Blocks.WATER.defaultBlockState() })).decorated(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, chance))));
	}

	public void addTropicsFlowers(BiomeGenerationSettings.Builder biome) {
		// TODO used a dummy config here for 1.15 - fix later
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.TROPICS_FLOWERS.get().configured(DefaultBiomeFeatures.BLUE_ORCHID_CONFIG).decorated(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(12))));
		biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(IRIS_CONFIG).decorated(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(10))));
	}
}
