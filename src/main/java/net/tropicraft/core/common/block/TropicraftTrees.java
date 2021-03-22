package net.tropicraft.core.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;

import javax.annotation.Nullable;
import java.util.Random;

public class TropicraftTrees {
    private static abstract class TropicraftTree extends Tree {

        protected abstract Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive);

        @Override
        public boolean growTree(ServerWorld world, ChunkGenerator generator, BlockPos pos, BlockState state, Random random) {
            Feature feature = getTropicraftTreeFeature(random, hasAdjacentFlower(world, pos));
            if (feature == null) {
                return false;
            } else {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
                if (feature.place(world, generator, random, pos, NoFeatureConfig.NONE)) {
                    return true;
                } else {
                    world.setBlock(pos, state, Constants.BlockFlags.NO_RERENDER);
                    return false;
                }
            }
        }

        private boolean hasAdjacentFlower(IWorld world, BlockPos origin) {
            for (BlockPos pos : BlockPos.Mutable.betweenClosed(origin.offset(-2, -1, -2), origin.offset(2, 1, 2))) {
                if (world.getBlockState(pos).is(BlockTags.FLOWERS)) {
                    return true;
                }
            }
            return false;
        }

        @Nullable
        @Override
        protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random random, boolean largeHive) {
            return null;
        }
    }

    public static final TropicraftTree GRAPEFRUIT = new TropicraftTree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.GRAPEFRUIT_TREE.get();
        }
    };

    public static final Tree LEMON = new TropicraftTree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.LEMON_TREE.get();
        }
    };

    public static final Tree LIME = new TropicraftTree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.LIME_TREE.get();
        }
    };

    public static final Tree ORANGE = new TropicraftTree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.ORANGE_TREE.get();
        }
    };

    public static final Tree RAINFOREST = new TropicraftTree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            final int treeType = random.nextInt(4);
            if (treeType == 0) {
                return TropicraftFeatures.TALL_TREE.get();
            } else if (treeType == 1) {
                return TropicraftFeatures.SMALL_TUALUNG.get();
            } else if (treeType == 2) {
                return TropicraftFeatures.UP_TREE.get();
            } else {
                return TropicraftFeatures.LARGE_TUALUNG.get();
            }
        }
    };

    public static final Tree PALM = new TropicraftTree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            final int palmType = random.nextInt(3);
            if (palmType == 0) {
                return TropicraftFeatures.NORMAL_PALM_TREE.get();
            } else if (palmType == 1) {
                return TropicraftFeatures.CURVED_PALM_TREE.get();
            } else {
                return TropicraftFeatures.LARGE_PALM_TREE.get();
            }
        }
    };
}
