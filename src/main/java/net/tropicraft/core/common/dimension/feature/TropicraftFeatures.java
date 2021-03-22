package net.tropicraft.core.common.dimension.feature;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.tropicraft.Constants;
import net.tropicraft.core.common.TropicraftTags;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.dimension.feature.config.HomeTreeBranchConfig;
import net.tropicraft.core.common.dimension.feature.config.RainforestVinesConfig;
import net.tropicraft.core.common.dimension.feature.jigsaw.SinkInGroundProcessor;
import net.tropicraft.core.common.dimension.feature.jigsaw.SmoothingGravityProcessor;
import net.tropicraft.core.common.dimension.feature.jigsaw.SteepPathProcessor;
import net.tropicraft.core.common.dimension.feature.jigsaw.StructureSupportsProcessor;

import java.util.function.Supplier;

public class TropicraftFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Constants.MODID);

	public static final RegistryObject<FruitTreeFeature> GRAPEFRUIT_TREE = register(
	        "grapefruit_tree", () -> new FruitTreeFeature(NoFeatureConfig.CODEC, TropicraftBlocks.ORANGE_SAPLING, () -> TropicraftBlocks.ORANGE_LEAVES.get().defaultBlockState()));
	public static final RegistryObject<FruitTreeFeature> ORANGE_TREE = register(
	        "orange_tree", () -> new FruitTreeFeature(NoFeatureConfig.CODEC, TropicraftBlocks.ORANGE_SAPLING, () -> TropicraftBlocks.ORANGE_LEAVES.get().defaultBlockState()));
	public static final RegistryObject<FruitTreeFeature> LEMON_TREE = register(
	        "lemon_tree", () -> new FruitTreeFeature(NoFeatureConfig.CODEC, TropicraftBlocks.LEMON_SAPLING, () -> TropicraftBlocks.LEMON_LEAVES.get().defaultBlockState()));
	public static final RegistryObject<FruitTreeFeature> LIME_TREE = register(
	        "lime_tree", () -> new FruitTreeFeature(NoFeatureConfig.CODEC, TropicraftBlocks.LIME_SAPLING, () -> TropicraftBlocks.LIME_LEAVES.get().defaultBlockState()));
	public static final RegistryObject<PalmTreeFeature> NORMAL_PALM_TREE = register(
	        "normal_palm_tree", () -> new NormalPalmTreeFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<PalmTreeFeature> CURVED_PALM_TREE = register(
	        "curved_palm_tree", () -> new CurvedPalmTreeFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<PalmTreeFeature> LARGE_PALM_TREE = register(
	        "large_palm_tree", () -> new LargePalmTreeFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<RainforestTreeFeature> UP_TREE = register(
	        "up_tree", () -> new UpTreeFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<RainforestTreeFeature> SMALL_TUALUNG = register(
	        "small_tualung", () -> new TualungFeature(NoFeatureConfig.CODEC, 16, 9));
	public static final RegistryObject<RainforestTreeFeature> LARGE_TUALUNG = register(
	        "large_tualung", () -> new TualungFeature(NoFeatureConfig.CODEC, 25, 11));
	public static final RegistryObject<RainforestTreeFeature> TALL_TREE = register(
	        "tall_tree", () -> new TallRainforestTreeFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<EIHFeature> EIH = register(
	        "eih", () -> new EIHFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<TropicsFlowersFeature> TROPICS_FLOWERS = register(
	        "tropics_flowers", () -> new TropicsFlowersFeature(BlockClusterFeatureConfig.CODEC, TropicraftTags.Blocks.TROPICS_FLOWERS));
	public static final RegistryObject<TropicsFlowersFeature> RAINFOREST_FLOWERS = register(
	        "rainforest_flowers", () -> new TropicsFlowersFeature(BlockClusterFeatureConfig.CODEC, TropicraftTags.Blocks.RAINFOREST_FLOWERS));
	public static final RegistryObject<UndergrowthFeature> UNDERGROWTH = register(
	        "undergrowth", () -> new UndergrowthFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<RainforestVinesFeature> VINES = register(
	        "rainforest_vines", () -> new RainforestVinesFeature(RainforestVinesConfig.CODEC));
	public static final RegistryObject<Structure<NoFeatureConfig>> VILLAGE = register(
	        "koa_village", () -> new KoaVillageStructure(NoFeatureConfig.CODEC));
	public static final RegistryObject<VolcanoFeature> VOLCANO = register(
	        "volcano", () -> new VolcanoFeature(NoFeatureConfig.CODEC));
	public static final RegistryObject<Structure<VillageConfig>> HOME_TREE = register(
			"home_tree", () -> new HomeTreeFeature(VillageConfig.CODEC));
	public static final RegistryObject<HomeTreeBranchFeature<HomeTreeBranchConfig>> HOME_TREE_BRANCH = register(
			"home_tree_branch", () -> new HomeTreeBranchFeature<>(HomeTreeBranchConfig.CODEC));
	public static final RegistryObject<CoffeePlantFeature> COFFEE_BUSH = register(
			"coffee_bush", () -> new CoffeePlantFeature(NoFeatureConfig.CODEC));

	public static final PlacementBehaviour KOA_PATH = PlacementBehaviour.create("KOA_PATH", Constants.MODID + ":koa_path",
            ImmutableList.of(new SmoothingGravityProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1), new SinkInGroundProcessor(), new SteepPathProcessor(), new StructureSupportsProcessor(false, TropicraftBlocks.BAMBOO_FENCE.getId())));

    private static <T extends Feature<?>> RegistryObject<T> register(final String name, final Supplier<T> sup) {
        return FEATURES.register(name, sup);
    }
}
