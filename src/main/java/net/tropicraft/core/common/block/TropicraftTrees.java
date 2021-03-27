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
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.tropicraft.core.common.dimension.feature.TropicraftFeatures;
import net.tropicraft.core.common.dimension.feature.config.FruitTreeConfig;

import javax.annotation.Nullable;
import java.util.Random;

public class TropicraftTrees {
	public static final Tree GRAPEFRUIT = create((random, beehive) ->
			TropicraftFeatures.FRUIT_TREE.get().configured(new FruitTreeConfig(TropicraftBlocks.GRAPEFRUIT_SAPLING, TropicraftBlocks.GRAPEFRUIT_LEAVES))
	);
	public static final Tree LEMON = create((random, beehive) ->
			TropicraftFeatures.FRUIT_TREE.get().configured(new FruitTreeConfig(TropicraftBlocks.LEMON_SAPLING, TropicraftBlocks.LEMON_LEAVES))
	);
	public static final Tree LIME = create((random, beehive) ->
			TropicraftFeatures.FRUIT_TREE.get().configured(new FruitTreeConfig(TropicraftBlocks.LIME_SAPLING, TropicraftBlocks.LIME_LEAVES))
	);
	public static final Tree ORANGE = create((random, beehive) ->
			TropicraftFeatures.FRUIT_TREE.get().configured(new FruitTreeConfig(TropicraftBlocks.ORANGE_SAPLING, TropicraftBlocks.ORANGE_LEAVES))
	);

	public static final Tree RAINFOREST = create((random, beehive) -> {
		final int treeType = random.nextInt(4);
		if (treeType == 0) {
			return TropicraftFeatures.TALL_TREE.get().configured(NoFeatureConfig.INSTANCE);
		} else if (treeType == 1) {
			return TropicraftFeatures.SMALL_TUALUNG.get().configured(NoFeatureConfig.INSTANCE);
		} else if (treeType == 2) {
			return TropicraftFeatures.UP_TREE.get().configured(NoFeatureConfig.INSTANCE);
		} else {
			return TropicraftFeatures.LARGE_TUALUNG.get().configured(NoFeatureConfig.INSTANCE);
		}
	});

	public static final Tree PALM = create((random, beehive) -> {
		final int palmType = random.nextInt(3);
		if (palmType == 0) {
			return TropicraftFeatures.NORMAL_PALM_TREE.get().configured(NoFeatureConfig.INSTANCE);
		} else if (palmType == 1) {
			return TropicraftFeatures.CURVED_PALM_TREE.get().configured(NoFeatureConfig.INSTANCE);
		} else {
			return TropicraftFeatures.LARGE_PALM_TREE.get().configured(NoFeatureConfig.INSTANCE);
		}
	});

	private static Tree create(FeatureProvider featureProvider) {
		return new Tree() {
			@Nullable
			@Override
			protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getConfiguredFeature(Random random, boolean beehive) {
				return null;
			}

			@Override
			public boolean growTree(ServerWorld world, ChunkGenerator generator, BlockPos pos, BlockState sapling, Random random) {
				ConfiguredFeature<?, ?> feature = featureProvider.getFeature(random, this.hasFlowers(world, pos));
				if (feature == null) {
					return false;
				}

				world.setBlock(pos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.NO_RERENDER);
				if (feature.place(world, generator, random, pos)) {
					return true;
				} else {
					world.setBlock(pos, sapling, Constants.BlockFlags.NO_RERENDER);
					return false;
				}
			}

			private boolean hasFlowers(IWorld world, BlockPos origin) {
				BlockPos min = origin.offset(-2, -1, -2);
				BlockPos max = origin.offset(2, 1, 2);
				for (BlockPos pos : BlockPos.Mutable.betweenClosed(min, max)) {
					if (world.getBlockState(pos).is(BlockTags.FLOWERS)) {
						return true;
					}
				}
				return false;
			}
		};
	}

	interface FeatureProvider {
		@Nullable
		ConfiguredFeature<?, ?> getFeature(Random random, boolean beehive);
	}
}
