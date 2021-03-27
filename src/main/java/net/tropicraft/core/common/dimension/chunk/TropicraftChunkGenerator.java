package net.tropicraft.core.common.dimension.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.function.Supplier;

// TODO: noise settings to move terrain up; top slide max = 0 instead of -10
public class TropicraftChunkGenerator extends NoiseChunkGenerator {
    public static final Codec<TropicraftChunkGenerator> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                BiomeProvider.CODEC.fieldOf("biome_source").forGetter(g -> g.biomeSource),
                Codec.LONG.fieldOf("seed").stable().forGetter(g -> g.seed),
                DimensionSettings.CODEC.fieldOf("settings").forGetter(g -> g.settings)
        ).apply(instance, instance.stable(TropicraftChunkGenerator::new));
    });

    private final VolcanoGenerator volcano;
    private final long seed;

    public TropicraftChunkGenerator(BiomeProvider biomeProvider, long seed, Supplier<DimensionSettings> settings) {
        super(biomeProvider, seed, settings);
        this.seed = seed;
        this.volcano = new VolcanoGenerator(seed, biomeSource);
    }

    @Override
    public int getSpawnHeight() {
        return 128;
    }

    @Override
    public int getSeaLevel() {
        return getSpawnHeight() - 1;
    }

    @Override
    public void fillFromNoise(IWorld world, StructureManager structures, IChunk chunk) {
        super.fillFromNoise(world, structures, chunk);

        ChunkPos chunkPos = chunk.getPos();
        volcano.generate(chunkPos.x, chunkPos.z, chunk, random);
    }

    @Override
    public int getBaseHeight(int x, int z, Type heightmapType) {
        int height = super.getBaseHeight(x, z, heightmapType);
        if (heightmapType != Type.OCEAN_FLOOR && heightmapType != Type.OCEAN_FLOOR_WG) {
            return Math.max(height, this.volcano.getVolcanoHeight(height, x, z));
        }
        return height;
    }
}
