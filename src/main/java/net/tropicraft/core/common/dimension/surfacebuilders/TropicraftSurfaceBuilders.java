package net.tropicraft.core.common.dimension.surfacebuilders;

import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tropicraft.Constants;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class TropicraftSurfaceBuilders {

    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, Constants.MODID);

    public static final TropicsSurfaceBuilder _TROPICS = new TropicsSurfaceBuilder(SurfaceBuilderConfig.CODEC);
    public static final RegistryObject<TropicsSurfaceBuilder> TROPICS = register(
            "tropics", () -> _TROPICS);
    
    public static final UnderwaterSurfaceBuilder _UNDERWATER = new UnderwaterSurfaceBuilder(SurfaceBuilderConfig.CODEC);
    public static final RegistryObject<UnderwaterSurfaceBuilder> UNDERWATER = register(
            "underwater", () -> _UNDERWATER);

    private static <T extends SurfaceBuilder<?>> RegistryObject<T> register(final String name, final Supplier<T> sup) {
        return SURFACE_BUILDERS.register(name, sup);
    }
}
