package net.tropicraft.core.common.dimension.config;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.LazyValue;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.tropicraft.core.common.block.BlockTropicraftSand;
import net.tropicraft.core.common.block.TropicraftBlocks;

public class TropicsBuilderConfigs {
    private static final LazyValue<BlockState> PURIFIED_SAND = new LazyValue<>(() -> TropicraftBlocks.PURIFIED_SAND.get().defaultBlockState());
    private static final LazyValue<BlockState> UNDERWATER_PURIFIED_SAND = new LazyValue<>(() -> PURIFIED_SAND.get().setValue(BlockTropicraftSand.UNDERWATER, true));

    public static final Supplier<SurfaceBuilderConfig> PURIFIED_SAND_CONFIG = () -> new SurfaceBuilderConfig(PURIFIED_SAND.get(), PURIFIED_SAND.get(), UNDERWATER_PURIFIED_SAND.get());
    public static final Supplier<SurfaceBuilderConfig> UNDERWATER_PURIFIED_SAND_CONFIG = () -> new SurfaceBuilderConfig(UNDERWATER_PURIFIED_SAND.get(), UNDERWATER_PURIFIED_SAND.get(), UNDERWATER_PURIFIED_SAND.get());

    public static final Supplier<SurfaceBuilderConfig> TROPICS_CONFIG = () -> new SurfaceBuilderConfig(Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.STONE.defaultBlockState());
}
