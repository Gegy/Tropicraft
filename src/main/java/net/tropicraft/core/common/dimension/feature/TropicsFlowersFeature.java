package net.tropicraft.core.common.dimension.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFlowersFeature;

import java.util.Collection;
import java.util.Random;

public class TropicsFlowersFeature extends DefaultFlowersFeature {
    private final ITag.INamedTag<Block> flowers;

    public TropicsFlowersFeature(Codec<BlockClusterFeatureConfig> codec, final ITag.INamedTag<Block> flowers) {
        super(codec);
        this.flowers = flowers;
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockClusterFeatureConfig config) {
        if (!flowers.getValues().isEmpty()) {
            return super.place(world, generator, rand, pos, config);
        }
        return false;
    }

    @Override
    public BlockState getRandomFlower(Random rand, BlockPos pos, BlockClusterFeatureConfig config) {
        final double noise = MathHelper.clamp((1.0D + Biome.BIOME_INFO_NOISE.getValue(pos.getX() / 48.0D, pos.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
        Collection<Block> allFlowers = flowers.getValues();
        return allFlowers.toArray(new Block[0])[(int) (noise * (double) allFlowers.size())].defaultBlockState();
    }
}
