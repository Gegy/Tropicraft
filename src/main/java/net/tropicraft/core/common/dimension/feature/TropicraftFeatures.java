package net.tropicraft.core.common.dimension.feature;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.GravityStructureProcessor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.tropicraft.Constants;
import net.tropicraft.Info;
import net.tropicraft.core.common.block.TropicraftBlocks;

public class TropicraftFeatures {

	public static final FruitTreeFeature GRAPEFRUIT_TREE = new FruitTreeFeature(NoFeatureConfig::deserialize, true, TropicraftBlocks.GRAPEFRUIT_LEAVES.getDefaultState());
	public static final FruitTreeFeature ORANGE_TREE = new FruitTreeFeature(NoFeatureConfig::deserialize, true, TropicraftBlocks.ORANGE_LEAVES.getDefaultState());
	public static final FruitTreeFeature LEMON_TREE = new FruitTreeFeature(NoFeatureConfig::deserialize, true, TropicraftBlocks.LEMON_LEAVES.getDefaultState());
	public static final FruitTreeFeature LIME_TREE = new FruitTreeFeature(NoFeatureConfig::deserialize, true, TropicraftBlocks.LIME_LEAVES.getDefaultState());
	public static final PalmTreeFeature NORMAL_PALM_TREE = new NormalPalmTreeFeature(NoFeatureConfig::deserialize, true);
	public static final PalmTreeFeature CURVED_PALM_TREE = new CurvedPalmTreeFeature(NoFeatureConfig::deserialize, true);
	public static final PalmTreeFeature LARGE_PALM_TREE = new LargePalmTreeFeature(NoFeatureConfig::deserialize, true);
	public static final RainforestTreeFeature UP_TREE = new UpTreeFeature(NoFeatureConfig::deserialize, true);
	public static final RainforestTreeFeature SMALL_TUALUNG = new TualungFeature(NoFeatureConfig::deserialize, true, 16, 9);
	public static final RainforestTreeFeature LARGE_TUALUNG = new TualungFeature(NoFeatureConfig::deserialize, true, 25, 11);
	public static final RainforestTreeFeature TALL_TREE = new TallRainforestTreeFeature(NoFeatureConfig::deserialize, true);
	public static final EIHFeature EIH = new EIHFeature(NoFeatureConfig::deserialize);
	public static final TropicsFlowersFeature TROPICS_FLOWERS = new TropicsFlowersFeature(NoFeatureConfig::deserialize, TropicraftBlocks.TROPICS_FLOWERS);
	public static final TropicsFlowersFeature RAINFOREST_FLOWERS = new TropicsFlowersFeature(NoFeatureConfig::deserialize, new Block[]{TropicraftBlocks.MAGIC_MUSHROOM});
	public static final UndergrowthFeature UNDERGROWTH = new UndergrowthFeature(NoFeatureConfig::deserialize);
	
	public static final Structure<NoFeatureConfig> VILLAGE = new KoaVillageStructure(NoFeatureConfig::deserialize);
	
    public static final PlacementBehaviour KOA_TOWN_CENTER = PlacementBehaviour.create("KOA_TOWN_CENTER", Info.MODID + ":koa_town_center",
            ImmutableList.of(new StructureSupportsProcessor(), new StructureVoidProcessor()));
    public static final PlacementBehaviour KOA_PATH = PlacementBehaviour.create("KOA_PATH", Info.MODID + ":koa_path",
            ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1), new SinkInGroundProcessor(), new SteepPathProcessor(), new StructureSupportsProcessor()));
    public static final PlacementBehaviour KOA_BUILDING = PlacementBehaviour.create("KOA_BUILDING", Info.MODID + ":koa_building",
            ImmutableList.of(new AdjustBuildingHeightProcessor(), new StructureSupportsProcessor(), new StructureVoidProcessor()));

    @EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onFeaturesRegistry(final RegistryEvent.Register<Feature<?>> event) {
        	register(event, GRAPEFRUIT_TREE, "grapefruit_tree");
        	register(event, ORANGE_TREE, "orange_tree");
        	register(event, LEMON_TREE, "lemon_tree");
        	register(event, LIME_TREE, "lime_tree");
        	register(event, NORMAL_PALM_TREE, "normal_palm_tree");
			register(event, CURVED_PALM_TREE, "curved_palm_tree");
			register(event, LARGE_PALM_TREE, "large_palm_tree");
			register(event, UP_TREE, "up_tree");
			register(event, SMALL_TUALUNG, "small_tualung");
			register(event, LARGE_TUALUNG, "large_tualung");
			register(event, TALL_TREE, "tall_tree");
			register(event, EIH, "eih");
			register(event, TROPICS_FLOWERS, "tropics_flowers");
			register(event, RAINFOREST_FLOWERS, "rainforest_flowers");
			register(event, UNDERGROWTH, "undergrowth");
			
			register(event, VILLAGE, "koa_village");
        }

        private static void register(final RegistryEvent.Register<Feature<?>> event, final Feature<?> feature, final String name) {
			event.getRegistry().register(feature.setRegistryName(new ResourceLocation(Constants.MODID, name)));
		}
    }
}
