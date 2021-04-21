package net.tropicraft.core.common.dimension.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.tropicraft.core.common.block.TropicraftBlocks;

import java.util.Random;
import java.util.function.Supplier;

import static net.tropicraft.core.common.dimension.feature.TropicraftFeatureUtil.goesBeyondWorldSize;
import static net.tropicraft.core.common.dimension.feature.TropicraftFeatureUtil.isBBAvailable;

public class FruitTreeFeature extends Feature<NoFeatureConfig> {

	private final Supplier<BlockState> WOOD_BLOCK = () -> Blocks.OAK_LOG.defaultBlockState();
	private final Supplier<BlockState> REGULAR_LEAF_BLOCK = () -> TropicraftBlocks.FRUIT_LEAVES.get().defaultBlockState();
	private final Supplier<BlockState> FRUIT_LEAF_BLOCK;
	private final Supplier<? extends SaplingBlock> sapling;

	public FruitTreeFeature(Codec<NoFeatureConfig> codec, Supplier<? extends SaplingBlock> sapling, Supplier<BlockState> fruitLeaf) {
		super(codec);
		this.sapling = sapling;
		FRUIT_LEAF_BLOCK = fruitLeaf;
	}

	protected SaplingBlock getSapling() {
	    return sapling.get();
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		pos = pos.immutable();
		int height = rand.nextInt(3) + 4;

		if (goesBeyondWorldSize(world, pos.getY(), height)) {
			return false;
		}

		if (!isBBAvailable(world, pos, height)) {
			return false;
		}

        if (!getSapling().canSurvive(getSapling().defaultBlockState(), world, pos.below())) {
			return false;
		}

		setDirtAt(world, pos);

		for (int y = (pos.getY() - 3) + height; y <= pos.getY() + height; y++) {
			int presizeMod = y - (pos.getY() + height);
			int size = 1 - presizeMod / 2;
			for (int x = pos.getX() - size; x <= pos.getX() + size; x++) {
				int localX = x - pos.getX();
				for (int z = pos.getZ() - size; z <= pos.getZ() + size; z++) {
					int localZ = z - pos.getZ();
					if ((Math.abs(localX) != size || Math.abs(localZ) != size || rand.nextInt(2) != 0 && presizeMod != 0) && TreeFeature.isAirOrLeaves(world, new BlockPos(x, y, z))) {
						BlockPos leafPos = new BlockPos(x, y, z);
						if (rand.nextBoolean()) {
							// Set fruit-bearing leaves here
							setBlock(world, leafPos, FRUIT_LEAF_BLOCK.get());
						} else {
							// Set plain fruit tree leaves here
							setBlock(world, leafPos, REGULAR_LEAF_BLOCK.get());
						}
					}
				}
			}
		}

		// Tree stem
		for (int y = 0; y < height; y++) {
			BlockPos logPos = pos.above(y);
			if (TreeFeature.validTreePos(world, logPos)) {
				setBlock(world, logPos, WOOD_BLOCK.get());
			}
		}

		return true;
	}

	protected static boolean isDirt(IWorldGenerationBaseReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> {
			Block block = state.getBlock();
			return isDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
		});
	}

	protected void setDirt(IWorldGenerationReader world, BlockPos pos) {
		if (!isDirt(world, pos)) {
			setBlock(world, pos, Blocks.DIRT.defaultBlockState());
		}
	}

	protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos) {
		setDirt(reader, pos);
	}
}
