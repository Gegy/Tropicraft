package net.tropicraft.core.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Builder {

    public static Supplier<Block> block(final Block.Properties properties) {
        return block(() -> properties);
    }
    
    public static Supplier<Block> block(final Supplier<Block.Properties> properties) {
        return block(Block::new, properties);
    }
    
    public static <T extends Block> Supplier<T> block(final Function<Block.Properties, T> ctor, final Block.Properties properties) {
        return block(ctor, () -> properties);
    }
    
    public static <T extends Block> Supplier<T> block(final Function<Block.Properties, T> ctor, final Supplier<Block.Properties> properties) {
        return () -> ctor.apply(properties.get());
    }
    
    public static Supplier<Block> ore(MaterialColor color) {
        return block(prop(Material.STONE, color).strength(3.0F, 3.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2));
    }
    
    public static Supplier<Block> oreBlock(MaterialColor color) {
        return block(prop(Material.METAL, color).sound(SoundType.METAL).strength(5.0F, 6.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2));
    }
    
    public static Supplier<TropicsFlowerBlock> flower(TropicraftFlower type) {
        return block(p -> new TropicsFlowerBlock(type.getEffect(), type.getEffectDuration(), type.getShape(), p), lazyProp(Blocks.POPPY.delegate));
    }

    public static Supplier<BlockTropicraftSand> sand(final MaterialColor color) {
        return sand(color, 0.5f, 0.5f);
    }

    public static Supplier<BlockTropicraftSand> sand(final MaterialColor color, final float hardness, final float resistance) {
        return block(BlockTropicraftSand::new, prop(Material.SAND, color).sound(SoundType.SAND).harvestTool(ToolType.SHOVEL).strength(hardness, resistance));
    }

    public static Supplier<RotatedPillarBlock> bundle(final Block.Properties properties) {
        return block(RotatedPillarBlock::new, properties);
    }

    public static Supplier<Block> plank(final MaterialColor color) {
        return block(prop(Material.WOOD, color).strength(2.0F, 3.0F).sound(SoundType.WOOD));
    }

    public static Supplier<RotatedPillarBlock> log(final MaterialColor topColor, final MaterialColor sideColor) {
        return block(p -> new RotatedPillarBlock(p), prop(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor).strength(2.0F).sound(SoundType.WOOD));
    }

    public static Supplier<RotatedPillarBlock> wood(final MaterialColor color) {
        return block(RotatedPillarBlock::new, prop(Material.WOOD, color).strength(2.0F).sound(SoundType.WOOD));
    }
    
    public static Supplier<StairsBlock> stairs(final RegistryObject<? extends Block> source) {
        return block(p -> new StairsBlock(source.lazyMap(Block::defaultBlockState), p), lazyProp(source));
    }

    public static Supplier<SlabBlock> slab(final Supplier<? extends Block> source) {
        return block(SlabBlock::new, lazyProp(source));
    }

    public static Supplier<LeavesBlock> leaves(boolean decay) {
        return block(decay ? LeavesBlock::new : TropicraftLeavesBlock::new, lazyProp(Blocks.OAK_LEAVES.delegate));
    }

    @SafeVarargs
    public static Supplier<SaplingBlock> sapling(final Tree tree, final Supplier<? extends Block>... validPlantBlocks) {
    	return block(p -> new SaplingBlock(tree, p) {
            protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos) {
                final Block block = state.getBlock();
                if (validPlantBlocks == null || validPlantBlocks.length == 0) {
                    return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND;
                } else {
                    return Arrays.stream(validPlantBlocks).map(Supplier::get).anyMatch(b -> b == block);
                }
            }
        }, lazyProp(Blocks.OAK_SAPLING.delegate));
    }

    public static Supplier<FenceBlock> fence(final Supplier<? extends Block> source) {
        return block(FenceBlock::new, lazyProp(source));
    }

    public static Supplier<FenceGateBlock> fenceGate(final Supplier<? extends Block> source) {
        return block(FenceGateBlock::new, lazyProp(source));
    }

    public static Supplier<WallBlock> wall(final Supplier<? extends Block> source) {
        return block(WallBlock::new, lazyProp(source));
    }

    public static Supplier<BongoDrumBlock> bongo(final BongoDrumBlock.Size bongoSize) {
        return block(p -> new BongoDrumBlock(bongoSize, p), prop(Material.WOOD, MaterialColor.TERRACOTTA_WHITE).strength(2.0F).sound(SoundType.WOOD));
    }
    
    public static Supplier<FlowerPotBlock> pot(final Supplier<FlowerPotBlock> emptyPot, final Supplier<? extends Block> flower, final Supplier<Block.Properties> properties) {
        return block(p -> new FlowerPotBlock(emptyPot, flower, p), properties);
    }

    public static Supplier<FlowerPotBlock> tropicraftPot() {
        return pot(null, Blocks.AIR.delegate, lazyProp(Material.DECORATION).then(p -> p.strength(0.2F, 5.0F).sound(SoundType.BAMBOO)));
    }

    public static Supplier<FlowerPotBlock> tropicraftPot(final Supplier<? extends Block> block) {
        return pot(TropicraftBlocks.BAMBOO_FLOWER_POT, block, lazyProp(Material.DECORATION).then(p -> p.strength(0.2F, 5.0F).sound(SoundType.BAMBOO)));
    }

    public static Supplier<FlowerPotBlock> vanillaPot(final Supplier<? extends Block> block) {
        return pot(() -> (FlowerPotBlock) Blocks.FLOWER_POT.delegate.get(), block, lazyProp(Blocks.FLOWER_POT.delegate));
    }

    private static Block.Properties prop(final Material material) {
        return Block.Properties.of(material);
    }

    private static Block.Properties prop(final Material material, final MaterialColor color) {
        return Block.Properties.of(material, color);
    }

    private static Block.Properties prop(final Material material, final Function<BlockState, MaterialColor> color) {
        return Block.Properties.of(material, color);
    }
    
    interface ComposableSupplier<T> extends Supplier<T> {
        
        default <R> ComposableSupplier<R> then(Function<T, R> func) {
            return () -> func.apply(get());
        }
    }

    private static ComposableSupplier<Block.Properties> lazyProp(final Material material) {
        return () -> prop(material);
    }

    private static ComposableSupplier<Block.Properties> lazyProp(final Material material, final MaterialColor color) {
        return () -> prop(material, color);
    }
    
    private static ComposableSupplier<Block.Properties> lazyProp(final Supplier<? extends Block> source) {
        return () -> {
            Objects.requireNonNull(source.get(), "Must register source block before using it");
            return Block.Properties.copy(source.get());
        };
    }
}
