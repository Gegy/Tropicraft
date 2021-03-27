package net.tropicraft.core.common.dimension.feature;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.tropicraft.core.common.block.TropicraftBlocks;
import net.tropicraft.core.common.data.WorldgenEntryConsumer;
import net.tropicraft.core.common.dimension.feature.config.FruitTreeConfig;
import net.tropicraft.core.common.dimension.feature.config.RainforestVinesConfig;

public final class TropicraftConfiguredFeatures {
	public final ConfiguredFeature<?, ?> grapefruitTree;
	public final ConfiguredFeature<?, ?> orangeTree;
	public final ConfiguredFeature<?, ?> lemonTree;
	public final ConfiguredFeature<?, ?> limeTree;
	public final ConfiguredFeature<?, ?> normalPalmTree;
	public final ConfiguredFeature<?, ?> curvedPalmTree;
	public final ConfiguredFeature<?, ?> largePalmTree;
	public final ConfiguredFeature<?, ?> upTree;
	public final ConfiguredFeature<?, ?> smallTualung;
	public final ConfiguredFeature<?, ?> largeTualung;
	public final ConfiguredFeature<?, ?> tallTree;
	public final ConfiguredFeature<?, ?> eih;
	public final ConfiguredFeature<?, ?> rainforestVines;

	public TropicraftConfiguredFeatures(WorldgenEntryConsumer<ConfiguredFeature<?, ?>> worldgen) {
		Register features = new Register(worldgen);

		this.grapefruitTree = features.register("grapefruit_tree", TropicraftFeatures.FRUIT_TREE,
				new FruitTreeConfig(TropicraftBlocks.GRAPEFRUIT_SAPLING, TropicraftBlocks.GRAPEFRUIT_LEAVES)
		);
		this.orangeTree = features.register("orange_tree", TropicraftFeatures.FRUIT_TREE,
				new FruitTreeConfig(TropicraftBlocks.ORANGE_SAPLING, TropicraftBlocks.ORANGE_LEAVES)
		);
		this.lemonTree = features.register("lemon_tree", TropicraftFeatures.FRUIT_TREE,
				new FruitTreeConfig(TropicraftBlocks.LEMON_SAPLING, TropicraftBlocks.LEMON_LEAVES)
		);
		this.limeTree = features.register("lime_tree", TropicraftFeatures.FRUIT_TREE,
				new FruitTreeConfig(TropicraftBlocks.LIME_SAPLING, TropicraftBlocks.LIME_LEAVES)
		);

		this.normalPalmTree = features.register("normal_palm_tree", TropicraftFeatures.NORMAL_PALM_TREE);
		this.curvedPalmTree = features.register("curved_palm_tree", TropicraftFeatures.CURVED_PALM_TREE);
		this.largePalmTree = features.register("large_palm_tree", TropicraftFeatures.LARGE_PALM_TREE);

		this.upTree = features.register("up_tree", TropicraftFeatures.UP_TREE);
		this.smallTualung = features.register("small_tualung", TropicraftFeatures.SMALL_TUALUNG);
		this.largeTualung = features.register("large_tualung", TropicraftFeatures.LARGE_TUALUNG);
		this.tallTree = features.register("tall_tree", TropicraftFeatures.TALL_TREE);
		this.eih = features.register("eih", TropicraftFeatures.EIH);

		this.rainforestVines = features.register("rainforest_vines", TropicraftFeatures.VINES, new RainforestVinesConfig());
	}

	static final class Register {
		private final WorldgenEntryConsumer<ConfiguredFeature<?, ?>> worldgen;

		Register(WorldgenEntryConsumer<ConfiguredFeature<?, ?>> worldgen) {
			this.worldgen = worldgen;
		}

		public <C extends IFeatureConfig, F extends Feature<C>> ConfiguredFeature<?, ?> register(String id, RegistryObject<F> feature, C config) {
			return this.worldgen.register(id, feature.get().configured(config));
		}

		public <F extends Feature<NoFeatureConfig>> ConfiguredFeature<?, ?> register(String id, RegistryObject<F> feature) {
			return this.register(id, feature, NoFeatureConfig.INSTANCE);
		}
	}
}
