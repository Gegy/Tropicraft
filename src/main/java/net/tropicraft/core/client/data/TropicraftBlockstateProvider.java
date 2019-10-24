package net.tropicraft.core.client.data;

import static net.minecraftforge.client.model.generators.ConfiguredModel.allRotations;
import static net.minecraftforge.client.model.generators.ConfiguredModel.allYRotations;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.tropicraft.Info;
import net.tropicraft.core.common.block.BlockTropicraftSand;
import net.tropicraft.core.common.block.TropicraftBlocks;

public class TropicraftBlockstateProvider extends BlockStateProvider {

    public TropicraftBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Info.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {        
        simpleBlock(TropicraftBlocks.CHUNK, applyRotations());
        
        // Ores and storage blocks
        simpleBlock(TropicraftBlocks.AZURITE_ORE);
        simpleBlock(TropicraftBlocks.EUDIALYTE_ORE);
        simpleBlock(TropicraftBlocks.MANGANESE_ORE);
        simpleBlock(TropicraftBlocks.SHAKA_ORE);
        simpleBlock(TropicraftBlocks.ZIRCON_ORE);
        
        simpleBlock(TropicraftBlocks.AZURITE_BLOCK);
        simpleBlock(TropicraftBlocks.EUDIALYTE_BLOCK);
        simpleBlock(TropicraftBlocks.ZIRCON_BLOCK);
        
        // All flowers
        TropicraftBlocks.FLOWERS.entrySet().forEach(e ->
            simpleBlock(e.getValue(), withExistingParent(e.getKey().getId(), "block/cross")
                    .texture("cross", "tropicraft:block/flower/" + e.getKey().getId())));
        
        // Purified sand
        ModelFile normal = cubeAll(TropicraftBlocks.PURIFIED_SAND);
        ModelFile calcified = cubeTop(TropicraftBlocks.PURIFIED_SAND, "calcified");
        ModelFile dune1 = cubeTop(TropicraftBlocks.PURIFIED_SAND, "dune1");
        ModelFile dune2 = cubeTop(TropicraftBlocks.PURIFIED_SAND, "dune2");
        ModelFile starfish = cubeTop(TropicraftBlocks.PURIFIED_SAND, "starfish");

        getVariantBuilder(TropicraftBlocks.PURIFIED_SAND.get())
            .partialState().with(BlockTropicraftSand.UNDERWATER, false)
                .addModels(allRotations(normal, false, 50))
                .addModels(allYRotations(calcified, 0, false, 5))
            .partialState().with(BlockTropicraftSand.UNDERWATER, true)
                .addModels(allRotations(normal, false, 50))
                .addModels(allYRotations(dune1, 0, false, 10))
                .addModels(allYRotations(dune2, 0, false, 10))
                .addModels(allYRotations(starfish, 0, false));
        
        // Other sands
        simpleBlock(TropicraftBlocks.PACKED_PURIFIED_SAND, applyRotations());
        simpleBlock(TropicraftBlocks.CORAL_SAND, applyRotations());
        simpleBlock(TropicraftBlocks.FOAMY_SAND, applyRotations());
        simpleBlock(TropicraftBlocks.VOLCANIC_SAND, applyRotations());
        simpleBlock(TropicraftBlocks.MINERAL_SAND, applyRotations());
        
        // Bundles
        axisBlock(TropicraftBlocks.BAMBOO_BUNDLE, "bamboo");
        axisBlock(TropicraftBlocks.THATCH_BUNDLE, "thatch");
        
        // Planks
        simpleBlock(TropicraftBlocks.MAHOGANY_PLANKS);
        simpleBlock(TropicraftBlocks.PALM_PLANKS);
        
        // Logs
        logBlock(TropicraftBlocks.MAHOGANY_LOG.get());
        logBlock(TropicraftBlocks.PALM_LOG.get());
        
        // Stairs
        stairsBlock(TropicraftBlocks.BAMBOO_STAIRS, "bamboo_side", "bamboo_end");
        stairsBlock(TropicraftBlocks.THATCH_STAIRS, "thatch_side", "thatch_end");
        stairsBlock(TropicraftBlocks.CHUNK_STAIRS, "chunk");
        stairsBlock(TropicraftBlocks.PALM_STAIRS, "palm_planks");
        stairsBlock(TropicraftBlocks.MAHOGANY_STAIRS, "mahogany_planks");

        ModelFile fuzzyThatch = fuzzyStairs("thatch_stairs_fuzzy", "thatch_side", "thatch_end", "thatch_grass");
        ModelFile fuzzyThatchOuter = fuzzyStairsOuter("thatch_stairs_fuzzy_outer", "thatch_side", "thatch_end", "thatch_grass");
        stairsBlock(TropicraftBlocks.THATCH_STAIRS_FUZZY.get(), fuzzyThatch, getExistingFile(modLoc("thatch_stairs_inner")), fuzzyThatchOuter);
        
        // Fences
        fenceBlock(TropicraftBlocks.BAMBOO_FENCE, "bamboo_side");
        fenceBlock(TropicraftBlocks.THATCH_FENCE, "thatch_side");
        fenceBlock(TropicraftBlocks.CHUNK_FENCE, "chunk");
        fenceBlock(TropicraftBlocks.PALM_FENCE, "palm_planks");
        fenceBlock(TropicraftBlocks.MAHOGANY_FENCE, "mahogany_planks");
    }

    private static Function<ModelFile, ConfiguredModel[]> applyRotations() {
        return f -> allRotations(f, false);
    }
    
    private static Function<ModelFile, ConfiguredModel[]> applyYRotations(int x) {
        return f -> allYRotations(f, x, false);
    }
    
    private String name(Supplier<? extends Block> block) {
        return block.get().getRegistryName().getPath();
    }
    
    private ResourceLocation blockTexture(Supplier<? extends Block> block) {
        ResourceLocation base = block.get().getRegistryName();
        return new ResourceLocation(base.getNamespace(), folder + "/" + base.getPath());
    }
    
    private ResourceLocation modBlockLoc(String texture) {
        return modLoc("block/" + texture);
    }
    
    private ModelFile cubeAll(Supplier<? extends Block> block) {
        return cubeAll(block.get());
    }
    
    private ModelFile cubeTop(Supplier<? extends Block> block, String suffix) {
        return cubeTop(name(block) + "_" + suffix, blockTexture(block), modBlockLoc(name(block) + "_" + suffix));
    }
    
    private void simpleBlock(Supplier<? extends Block> block) {
        simpleBlock(block.get());
    }
    
    private void simpleBlock(Supplier<? extends Block> block, ModelFile model) {
        simpleBlock(block.get(), model);
    }
    
    private void simpleBlock(Supplier<? extends Block> block, Function<ModelFile, ConfiguredModel[]> expander) {
        simpleBlock(block.get(), expander);
    }
    
    private void axisBlock(Supplier<? extends RotatedPillarBlock> block, String texture) {
        axisBlock(block.get(), modBlockLoc(texture));
    }
    
    private void fenceBlock(Supplier<? extends FenceBlock> block, String texture) {
        fenceBlock(block.get(), modBlockLoc(texture));
    }
    
    private void stairsBlock(Supplier<? extends StairsBlock> block, String name) {
        stairsBlock(block, name, name);
    }
    
    private void stairsBlock(Supplier<? extends StairsBlock> block, String side, String topBottom) {
        stairsBlock(block.get(), modBlockLoc(side), modBlockLoc(topBottom), modBlockLoc(topBottom));
    }
    
    private ModelFile fuzzyStairs(String name, String parent, String side, String end, String cross) {
        return withExistingParent(name, modLoc(parent))
                .texture("side", modBlockLoc(side))
                .texture("bottom", modBlockLoc(end))
                .texture("top", modBlockLoc(end))
                .texture("cross", modBlockLoc(cross));
    }
    
    private ModelFile fuzzyStairs(String name, String side, String end, String cross) {
        return fuzzyStairs(name, "stairs_fuzzy", side, end, cross);
    }
    
    private ModelFile fuzzyStairsOuter(String name, String side, String end, String cross) {
        return fuzzyStairs(name, "stairs_fuzzy_outer", side, end, cross);
    }
}
