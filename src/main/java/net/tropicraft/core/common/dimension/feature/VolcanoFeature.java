package net.tropicraft.core.common.dimension.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.serialization.Codec;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.dimension.chunk.VolcanoGenerator;

import java.util.Random;
import java.util.function.Function;

import static net.tropicraft.core.common.dimension.feature.TropicraftFeatureUtil.goesBeyondWorldSize;

/**
 * Feature class that searches for a volcano in the chunk and places
 * a Volcano tile entity if necessary.
 */
public class VolcanoFeature extends Structure<NoFeatureConfig> {

    public VolcanoFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        int chunkX = pos.getX() >> 4;
        int chunkY = pos.getZ() >> 4;

        BlockPos volcanoCoords = VolcanoGenerator.getVolcanoNear(generator, chunkX, chunkY);

        if (volcanoCoords != null) {
            BlockPos posVolcanoTE = new BlockPos(volcanoCoords.getX(), 1, volcanoCoords.getZ());

            // Doesn't go out of chunk boundaries
            if (posVolcanoTE.getX() > pos.getX() + 15 || posVolcanoTE.getX() < pos.getX() || posVolcanoTE.getZ() > pos.getZ() + 15 || posVolcanoTE.getZ() < pos.getZ()) {
                return false;
            }

            if (goesBeyondWorldSize(worldIn, posVolcanoTE.getY(), 1)) {
                return false;
            }

            if (worldIn.getBlockState(posVolcanoTE).getBlock() != TropicraftBlocks.VOLCANO.get()) {
                worldIn.setBlockState(posVolcanoTE, TropicraftBlocks.VOLCANO.get().defaultBlockState(), 3);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> chunkGen, Random randIn, int chunkX, int chunkZ, Biome biomeIn) {
        return VolcanoGenerator.canGenVolcanoAtCoords(chunkGen, chunkX, chunkZ) > 0;
    }
    @Override
    public IStartFactory getStartFactory() {
        return Start::new;
    }

    @Override
    public String getStructureName() {
        return TropicraftFeatures.VOLCANO.getId().toString();
    }

    @Override
    public int getSize() {
        return 0;
    }

    public static class Start extends StructureStart {

        public Start(Structure<?> p_i51110_1_, int p_i51110_2_, int p_i51110_3_, MutableBoundingBox p_i51110_5_, int p_i51110_6_, long p_i51110_7_) {
            super(p_i51110_1_, p_i51110_2_, p_i51110_3_, p_i51110_5_, p_i51110_6_, p_i51110_7_);
        }

        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            // Add no components, this doesn't generate anything, it's just for /locate
        }
        
        @Override
        public boolean isValid() {
            return true;
        }
    }
}
