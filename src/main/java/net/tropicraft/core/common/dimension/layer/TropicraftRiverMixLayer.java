package net.tropicraft.core.common.dimension.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum TropicraftRiverMixLayer implements IAreaTransformer2, IDimOffset0Transformer {
    INSTANCE;

    @Override
    public int applyPixel(INoiseRandom iNoiseRandom, IArea parent1, IArea parent2, int x, int y) {
        final int biome = parent1.get(getParentX(x), getParentY(y));
        final int river = parent2.get(getParentX(x), getParentY(y));

        if (!TropicraftLayerUtil.isOcean(biome)) {
            if (TropicraftLayerUtil.isRiver(river)) {
                return river;
            }
        }

        return biome;
    }
}
