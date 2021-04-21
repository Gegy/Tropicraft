package net.tropicraft.core.common.dimension.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.util.Constants;
import net.tropicraft.core.common.block.TropicraftBlocks;

import java.util.Random;

public class CoffeePlantFeature extends Feature<NoFeatureConfig> {
    public static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
    public static final BlockState COFE = TropicraftBlocks.COFFEE_BUSH.get().getStateForAge(6);
    public static final BlockState FARMLAND = Blocks.FARMLAND.defaultBlockState();
    public static final BlockState WATER = Blocks.WATER.defaultBlockState();

    public CoffeePlantFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig config) {
        final BlockPos genPos = new BlockPos(
                (pos.getX() + random.nextInt(8)) - random.nextInt(8),
                pos.getY(),
                (pos.getZ() + random.nextInt(8)) - random.nextInt(8)
        );

        // Find a suitable place to generate
        if (!world.isEmptyBlock(genPos) || world.getBlockState(genPos.below()).getBlock() != GRASS_BLOCK.getBlock() || world.isEmptyBlock(genPos.below(2))) {
            return false;
        }

        Direction viableDirection = null;

        // Scan for potential water spot
        for (final Direction dir : Direction.Plane.HORIZONTAL) {
            final int neighborx = genPos.getX() + dir.getStepX();
            final int neighborz = genPos.getZ() + dir.getStepZ();

            if (!world.isEmptyBlock(new BlockPos(neighborx, pos.getY() - 1, neighborz))) {
                viableDirection = dir;
                break;
            }
        }

        if (viableDirection == null) {
            return false;
        }

        final BlockPos waterPos = new BlockPos(genPos.getX() + viableDirection.getStepX(), pos.getY() - 1, genPos.getZ() + viableDirection.getStepZ());
        world.setBlock(waterPos, WATER, Constants.BlockFlags.DEFAULT);
        world.setBlock(genPos.below(), FARMLAND, Constants.BlockFlags.DEFAULT);

        for (final Direction dir : Direction.Plane.HORIZONTAL) {
            world.setBlock(waterPos.relative(dir), GRASS_BLOCK, Constants.BlockFlags.DEFAULT);
        }

        for (int i = 0; i < 3; ++i) {
            final BlockPos upPos = genPos.above(i);
            if (world.isEmptyBlock(upPos)) {
                world.setBlock(upPos, COFE, Constants.BlockFlags.DEFAULT);
            } else {
                break;
            }
        }

        return true;
    }
}
