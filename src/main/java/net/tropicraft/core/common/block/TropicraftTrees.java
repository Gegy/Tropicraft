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
    // TODO: how do we reference generated features here?
    public static final Tree GRAPEFRUIT = new Tree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.GRAPEFRUIT_TREE.get();
        }
    };

    public static final Tree LEMON = new Tree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.LEMON_TREE.get();
        }
    };

    public static final Tree LIME = new Tree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.LIME_TREE.get();
        }
    };

    public static final Tree ORANGE = new Tree() {
        @Override
        protected Feature<? extends NoFeatureConfig> getTropicraftTreeFeature(Random random, boolean generateBeehive) {
            return TropicraftFeatures.ORANGE_TREE.get();
        }
    };

    public static final Tree RAINFOREST = new Tree() {
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

    public static final Tree PALM = new Tree() {
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
