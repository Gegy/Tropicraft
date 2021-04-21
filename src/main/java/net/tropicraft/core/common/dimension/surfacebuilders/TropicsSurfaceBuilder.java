package net.tropicraft.core.common.dimension.surfacebuilders;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.surfacebuilders.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.tropicraft.core.common.dimension.config.TropicsBuilderConfigs;

import java.util.Random;

public class TropicsSurfaceBuilder extends DefaultSurfaceBuilder {
    public TropicsSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

	@Override
    public void apply(Random random, IChunk chunk, Biome biome, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
        if (noise > 1.5) {
        	if (chunk.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, x, z) + 1 >= seaLevel) {
        		config = TropicsBuilderConfigs.PURIFIED_SAND_CONFIG.get();
        	} else {
            	config = TropicsBuilderConfigs.UNDERWATER_PURIFIED_SAND_CONFIG.get();
            }
        }
        super.apply(random, chunk, biome, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
    }
}
