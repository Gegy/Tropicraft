package net.tropicraft.core.common.dimension.feature.pools;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.jigsaw.*;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.tropicraft.Constants;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;
import net.tropicraft.core.common.dimension.feature.config.HomeTreeBranchConfig;
import net.tropicraft.core.common.dimension.feature.jigsaw.AirToCaveAirProcessor;
import net.tropicraft.core.common.dimension.feature.jigsaw.FixedSingleJigsawPiece;
import net.tropicraft.core.common.dimension.feature.jigsaw.NoRotateSingleJigsawPiece;
import net.tropicraft.core.common.dimension.feature.jigsaw.StructureSupportsProcessor;

@SuppressWarnings("deprecation")
public class HomeTreePools {

    public static void init() {
    }
        
    static {
        // TODO add SpawnerProcessor
        StructureProcessorList baseProcessors = registerProcessorList("home_tree_base", ImmutableList.of(new AirToCaveAirProcessor()));

        StructureProcessorList startProcessors = registerProcessorList("home_tree_start", ImmutableList.<StructureProcessor>builder()
                .addAll(baseProcessors.list())
                .add(new StructureSupportsProcessor(true, ImmutableList.of(TropicraftBlocks.MAHOGANY_LOG.getId())))
                .build()
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/starts"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/trunks/bottom/trunk_0", startProcessors), 1),
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/trunks/bottom/trunk_1", startProcessors), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/roofs"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/roofs/roof_0", baseProcessors), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/dummy"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/dummy", baseProcessors), 1),
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/outer_dummy", baseProcessors), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/trunks/middle"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/trunks/middle/trunk_0", baseProcessors), 1),
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/trunks/middle/trunk_1", baseProcessors), 1),
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/trunks/middle/trunk_1_iguanas", baseProcessors), 1),
                Pair.of(FixedSingleJigsawPiece.create(Constants.MODID + ":home_tree/trunks/middle/trunk_1_ashen", baseProcessors), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/trunks/top"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(NoRotateSingleJigsawPiece.createNoRotate(Constants.MODID + ":home_tree/trunks/top/trunk_0", baseProcessors), 1),
                Pair.of(NoRotateSingleJigsawPiece.createNoRotate(Constants.MODID + ":home_tree/trunks/top/trunk_1", baseProcessors), 1),
                Pair.of(NoRotateSingleJigsawPiece.createNoRotate(Constants.MODID + ":home_tree/trunks/top/trunk_2", baseProcessors), 1),
                Pair.of(NoRotateSingleJigsawPiece.createNoRotate(Constants.MODID + ":home_tree/trunks/top/trunk_3", baseProcessors), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        // 0 = south
        // 90 = east
        // 180 = north
        // 270 = west
        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/south"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(-30, 30))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(0, 0))), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/southeast"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(30, 60))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(45, 45))), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/east"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(60, 120))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(90, 90))), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/northeast"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(120, 150))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(135, 135))), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/north"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(150, 210))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(180, 180))), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/northwest"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(210, 240))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(225, 225))), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/west"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(240, 300))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(270, 270))), 1)
            ),
            PlacementBehaviour.RIGID)
        );

        JigsawPatternRegistry.register(new JigsawPattern(
            new ResourceLocation(Constants.MODID, "home_tree/branches/southwest"),
            new ResourceLocation("empty"),
            ImmutableList.of(
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(300, 330))), 4),
                Pair.of(JigsawPiece.feature(new ConfiguredFeature<>(TropicraftFeatures.HOME_TREE_BRANCH.get(), new HomeTreeBranchConfig(315, 315))), 1)
            ),
            PlacementBehaviour.RIGID)
        );
    }

    private static StructureProcessorList registerProcessorList(String id, ImmutableList<StructureProcessor> processors) {
        StructureProcessorList list = new StructureProcessorList(processors);
        return WorldGenRegistries.register(WorldGenRegistries.PROCESSOR_LIST, new ResourceLocation(Constants.MODID, id), list);
    }
}
