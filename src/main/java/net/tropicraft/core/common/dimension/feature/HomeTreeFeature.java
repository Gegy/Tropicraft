package net.tropicraft.core.common.dimension.feature;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.FeatureJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.tropicraft.Constants;
import net.tropicraft.core.common.dimension.biome.TropicraftRainforestBiome;
import net.tropicraft.core.common.dimension.chunk.TropicraftChunkGenerator;
import net.tropicraft.core.common.dimension.feature.jigsaw.NoRotateSingleJigsawPiece;
import net.tropicraft.core.common.dimension.feature.pools.HomeTreePools;

public class HomeTreeFeature extends Structure<VillageConfig> {
    public HomeTreeFeature(Codec<VillageConfig> codec) {
        super(codec);
    }

    @Override
	public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> generatorIn, Random randIn, int chunkX, int chunkZ, Biome biomeIn) {
    	if (!biomeIn.hasStructure(this)) return false;
        ChunkPos chunkpos = this.getStartPositionForPosition(generatorIn, randIn, chunkX, chunkZ, 0, 0);
        if (chunkX == chunkpos.x && chunkZ == chunkpos.z) {
           BlockPos pos = new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8);
           int centerY = generatorIn.getHeight(pos.getX(), pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
           return isValid(generatorIn, pos.add(-4, 0, -4), centerY) &&
                  isValid(generatorIn, pos.add(-4, 0, 4), centerY) &&
                  isValid(generatorIn, pos.add(4, 0, 4), centerY) &&
                  isValid(generatorIn, pos.add(4, 0, -4), centerY);
        } else {
            return false;
        }
    }

    private boolean isValid(ChunkGenerator<?> chunkGen, BlockPos pos, int startY) {
        int y = chunkGen.getHeight(pos.getX(), pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        Biome biome = chunkGen.getBiomeProvider().getNoiseBiome(pos.getX(), startY, pos.getZ());
        return chunkGen.hasStructure(biome, TropicraftFeatures.HOME_TREE.get())
                && y >= chunkGen.getSeaLevel()
                && Math.abs(y - startY) < 10
                && y < 150
                && y > chunkGen.getSeaLevel() + 2
                && biome instanceof TropicraftRainforestBiome;
    }

    @Override
    public IStartFactory<VillageConfig> getStartFactory() {
        return Start::new;
    }

    private static final IStructurePieceType TYPE = IStructurePieceType.register(HomeTreePiece::new, Constants.MODID + ":home_tree");

    public static class Start extends StructureStart<VillageConfig> {

        public Start(Structure<?> p_i51110_1_, int p_i51110_2_, int p_i51110_3_, MutableBoundingBox p_i51110_5_, int p_i51110_6_, long p_i51110_7_) {
            super(p_i51110_1_, p_i51110_2_, p_i51110_3_, p_i51110_5_, p_i51110_6_, p_i51110_7_);
        }

        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            final BlockPos pos = new BlockPos(chunkX * 16, -5, chunkZ * 16);
            VillageConfig config = generator.getStructureConfig(biomeIn, TropicraftFeatures.HOME_TREE.get());
            HomeTreePools.init();
            JigsawManager.addPieces(config.startPool, config.size, HomeTreePiece::new, generator, templateManagerIn, pos, this.components, this.rand);
            this.recalculateStructureSize();
        }
        
        @Override
        protected void recalculateStructureSize() {
            super.recalculateStructureSize();
            int margin = 24; // Double vanilla's margin
            this.bounds.minX -= margin;
            this.bounds.minY -= margin;
            this.bounds.minZ -= margin;
            this.bounds.maxX += margin;
            this.bounds.maxY += margin;
            this.bounds.maxZ += margin;
         }
    }

    public static class HomeTreePiece extends AbstractVillagePiece {
        
        public HomeTreePiece(TemplateManager p_i50890_1_, JigsawPiece p_i50890_2_, BlockPos p_i50890_3_, int p_i50890_4_, Rotation p_i50890_5_, MutableBoundingBox p_i50890_6_) {
            super(TYPE, p_i50890_1_, p_i50890_2_, p_i50890_3_, p_i50890_4_, p_i50890_5_, p_i50890_6_);
        }

        public HomeTreePiece(TemplateManager p_i50891_1_, CompoundNBT p_i50891_2_) {
            super(p_i50891_1_, p_i50891_2_, TYPE);
        }
        
        @Override
        public MutableBoundingBox getBoundingBox() {
            if (this.jigsawPiece instanceof FeatureJigsawPiece) {
                MutableBoundingBox ret = super.getBoundingBox();
                ret = new MutableBoundingBox(ret);
                ret.minX -= 32;
                ret.minY -= 32;
                ret.minZ -= 32;
                ret.maxX += 32;
                ret.maxY += 32;
                ret.maxZ += 32;
            }
            return super.getBoundingBox();
        }
        
        @Override
        public Rotation getRotation() {
            if (this.jigsawPiece instanceof NoRotateSingleJigsawPiece) {
                return Rotation.NONE;
            }
            return super.getRotation();
        }
    }
}
