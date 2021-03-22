package net.tropicraft.core.common.dimension.biome;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockWithContextConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.dimension.carver.TropicraftCarvers;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;
import net.tropicraft.core.common.dimension.feature.config.RainforestVinesConfig;

public class DefaultTropicsFeatures {

    public static final BlockClusterFeatureConfig IRIS_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(TropicraftBlocks.IRIS.get().defaultBlockState()), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
    public static final BlockClusterFeatureConfig PINEAPPLE_CONFIG = (new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(TropicraftBlocks.PINEAPPLE.get().defaultBlockState()), new DoublePlantBlockPlacer())).tries(64).noProjection().build();
    
    public static void addCarvers(BiomeGenerationSettings.Builder biome) {
        biome.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(TropicraftCarvers.CAVE.get(), new ProbabilityConfig(0.25F)));
        biome.withCarver(GenerationStage.Carving.AIR, Biome.createCarver(TropicraftCarvers.CANYON.get(), new ProbabilityConfig(0.02F)));
    }

    public static void addUnderwaterCarvers(BiomeGenerationSettings.Builder biome) {
        biome.withCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(TropicraftCarvers.UNDERWATER_CANYON.get(), new ProbabilityConfig(0.02F)));
        biome.withCarver(GenerationStage.Carving.LIQUID, Biome.createCarver(TropicraftCarvers.UNDERWATER_CAVE.get(), new ProbabilityConfig(0.15F)));
    }
    
    public static void addUndergroundSeagrass(BiomeGenerationSettings.Builder biome) {
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.withConfiguration(new BlockWithContextConfig(Blocks.SEAGRASS.defaultBlockState(), new BlockState[]{Blocks.STONE.defaultBlockState()}, new BlockState[]{Blocks.WATER.defaultBlockState()}, new BlockState[]{Blocks.WATER.defaultBlockState()})).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.1F))));
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.withConfiguration(new BlockWithContextConfig(Blocks.SEAGRASS.defaultBlockState(), new BlockState[]{Blocks.DIRT.defaultBlockState()}, new BlockState[]{Blocks.WATER.defaultBlockState()}, new BlockState[]{Blocks.WATER.defaultBlockState()})).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, 0.5F))));
    }
    
    public static void addUndergroundPickles(BiomeGenerationSettings.Builder biome) {
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

    private static void addPickleFeature(BiomeGenerationSettings.Builder biome, int pickles, BlockState placeOn, float chance) {
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.withConfiguration(new BlockWithContextConfig(Blocks.SEA_PICKLE.defaultBlockState().with(SeaPickleBlock.PICKLES, pickles), new BlockState[]{placeOn}, new BlockState[]{Blocks.WATER.defaultBlockState()}, new BlockState[]{Blocks.WATER.defaultBlockState()})).withPlacement(Placement.CARVING_MASK.configure(new CaveEdgeConfig(GenerationStage.Carving.LIQUID, chance))));
    }

    public static void addRainforestPlants(BiomeGenerationSettings.Builder biome) {
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.MELON_PATCH_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(1))));
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.VINES.get().withConfiguration(new RainforestVinesConfig()).withPlacement(Placement.COUNT_HEIGHT_64.configure(new FrequencyConfig(50))));
    }
    
    public static void addTropicsGems(BiomeGenerationSettings.Builder biome) {
        biome.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, TropicraftBlocks.AZURITE_ORE.get().defaultBlockState(), 8)).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(3, 100, 0, 128))));
        biome.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, TropicraftBlocks.EUDIALYTE_ORE.get().defaultBlockState(), 12)).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(10, 100, 0, 128))));
        biome.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, TropicraftBlocks.ZIRCON_ORE.get().defaultBlockState(), 14)).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(15, 100, 0, 128))));
    }
    
    public static void addTropicsMetals(BiomeGenerationSettings.Builder biome) {
        biome.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, TropicraftBlocks.MANGANESE_ORE.get().defaultBlockState(), 10)).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(8, 32, 0, 32))));
        biome.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, TropicraftBlocks.SHAKA_ORE.get().defaultBlockState(), 8)).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(6, 0, 0, 32))));
    }

    public static void addPalmTrees(BiomeGenerationSettings.Builder biome) {
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.NORMAL_PALM_TREE.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.2F, 1))));
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.CURVED_PALM_TREE.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.2F, 1))));
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.LARGE_PALM_TREE.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(0, 0.2F, 1))));
    }
    
    public static void addTropicsFlowers(BiomeGenerationSettings.Builder biome) {
        // TODO used a dummy config here for 1.15 - fix later
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, TropicraftFeatures.TROPICS_FLOWERS.get().withConfiguration(DefaultBiomeFeatures.BLUE_ORCHID_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(12))));
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(IRIS_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(10))));
    }
    
    public static void addPineapples(BiomeGenerationSettings.Builder biome) {
        biome.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(PINEAPPLE_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(1))));
    }
}
