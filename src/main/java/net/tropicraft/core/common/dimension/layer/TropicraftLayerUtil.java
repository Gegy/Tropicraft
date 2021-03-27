package net.tropicraft.core.common.dimension.layer;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.tropicraft.Constants;
import net.tropicraft.core.common.dimension.biome.TropicraftBiomes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;

@EventBusSubscriber(modid = Constants.MODID)
@SuppressWarnings("deprecation")
public class TropicraftLayerUtil {

    private static final List<LazyInt> BIOME_IDS = new ArrayList<>();

    protected static final LazyInt OCEAN_ID = lazyId(TropicraftBiomes.TROPICS_OCEAN);
    protected static final LazyInt KELP_FOREST_ID = lazyId(TropicraftBiomes.KELP_FOREST);
    protected static final LazyInt LAND_ID = lazyId(TropicraftBiomes.TROPICS);
    protected static final LazyInt RIVER_ID = lazyId(TropicraftBiomes.TROPICS_RIVER);
    protected static final LazyInt BEACH_ID = lazyId(TropicraftBiomes.TROPICS_BEACH);
    protected static final LazyInt ISLAND_MOUNTAINS_ID = lazyId(TropicraftBiomes.RAINFOREST_ISLAND_MOUNTAINS);
    protected static final LazyInt RAINFOREST_PLAINS_ID = lazyId(TropicraftBiomes.RAINFOREST_PLAINS);
    protected static final LazyInt RAINFOREST_HILLS_ID = lazyId(TropicraftBiomes.RAINFOREST_HILLS);
    protected static final LazyInt RAINFOREST_MOUNTAINS_ID = lazyId(TropicraftBiomes.RAINFOREST_MOUNTAINS);
    protected static final LazyInt[] TROPICS_LAND_IDS = new LazyInt[]{LAND_ID, RAINFOREST_PLAINS_ID};
    protected static final LazyInt[] RAINFOREST_IDS = new LazyInt[] {
      //      RAINFOREST_PLAINS_ID,
            RAINFOREST_HILLS_ID,
            RAINFOREST_MOUNTAINS_ID,
            //RAINFOREST_ISLAND_MOUNTAINS_ID
    };

    private static LazyInt lazyId(RegistryKey<Biome> biomeKey) {
        LazyInt ret = new LazyInt(() -> {
            Biome biome = WorldGenRegistries.BIOME.get(biomeKey);
            return WorldGenRegistries.BIOME.getId(biome);
        });
        BIOME_IDS.add(ret);
        return ret;
    }

    public static Layer buildTropicsProcedure(long seed) {
        final IAreaFactory<LazyArea> noiseLayer = buildTropicsProcedure(procedure -> new LazyAreaLayerContext(25, seed, procedure));
        return new Layer(noiseLayer);
    }

    private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> buildTropicsProcedure(final LongFunction<C> context) {
        IAreaFactory<T> islandLayer = TropicsIslandLayer.INSTANCE.run(context.apply(1));
        IAreaFactory<T> fuzzyZoomLayer = ZoomLayer.FUZZY.run(context.apply(2000), islandLayer);
        IAreaFactory<T> addIslandLayer = TropicraftAddIslandLayer.BASIC_3.run(context.apply(3), fuzzyZoomLayer);
        IAreaFactory<T> zoomLayer = ZoomLayer.NORMAL.run(context.apply(2000), addIslandLayer);

        IAreaFactory<T> oceanLayer = TropicraftAddInlandLayer.INSTANCE.run(context.apply(9), zoomLayer);
        oceanLayer = ZoomLayer.NORMAL.run(context.apply(9), oceanLayer);
        addIslandLayer = TropicraftAddIslandLayer.RAINFOREST_13.run(context.apply(6), oceanLayer);
        zoomLayer = ZoomLayer.NORMAL.run(context.apply(2001), addIslandLayer);
        zoomLayer = ZoomLayer.NORMAL.run(context.apply(2004), zoomLayer);
        addIslandLayer = TropicraftAddIslandLayer.BASIC_2.run(context.apply(8), zoomLayer);

        IAreaFactory<T> biomeLayerGen = TropicraftBiomesLayer.INSTANCE.run(context.apply(15), addIslandLayer);
        IAreaFactory<T> oceanLayerGen = TropicraftAddWeightedSubBiomesLayer.OCEANS.run(context.apply(16), biomeLayerGen);
        IAreaFactory<T> hillsLayerGen = TropicraftAddSubBiomesLayer.RAINFOREST.run(context.apply(17), oceanLayerGen);
        zoomLayer = ZoomLayer.NORMAL.run(context.apply(2002), hillsLayerGen);

        IAreaFactory<T> riverLayer = zoomLayer;
        riverLayer = TropicraftRiverInitLayer.INSTANCE.run(context.apply(12), riverLayer);
        riverLayer = magnify(2007, ZoomLayer.NORMAL, riverLayer, 5, context);
        riverLayer = TropicraftRiverLayer.INSTANCE.run(context.apply(13), riverLayer);
        riverLayer = SmoothLayer.INSTANCE.run(context.apply(2008L), riverLayer);

        IAreaFactory<T> magnifyLayer = magnify(2007L, ZoomLayer.NORMAL, zoomLayer, 3, context);
        IAreaFactory<T> biomeLayer = TropicraftBeachLayer.INSTANCE.run(context.apply(20), magnifyLayer);
        biomeLayer = magnify(20, ZoomLayer.NORMAL, biomeLayer, 2, context);

        biomeLayer = SmoothLayer.INSTANCE.run(context.apply(17L), biomeLayer);
        biomeLayer = TropicraftRiverMixLayer.INSTANCE.run(context.apply(17), biomeLayer, riverLayer);

        return biomeLayer;
    }

    private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> magnify(final long seed, final IAreaTransformer1 zoomLayer, final IAreaFactory<T> layer, final int count, final LongFunction<C> context) {
        IAreaFactory<T> result = layer;
        for (int i = 0; i < count; i++) {
            result = zoomLayer.run(context.apply(seed + i), result);
        }
        return result;
    }

    public static boolean isOcean(final int biome) {
        return biome == OCEAN_ID.getAsInt() || biome == KELP_FOREST_ID.getAsInt();
    }

    public static boolean isRiver(final int biome) {
        return biome == RIVER_ID.getAsInt();
    }

    public static boolean isLand(final int biome) {
        return biome == LAND_ID.getAsInt();
    }

    @SubscribeEvent
    public static void onRegistryRemap(FMLModIdMappingEvent event) {
        BIOME_IDS.forEach(LazyInt::invalidate);
    }
}
