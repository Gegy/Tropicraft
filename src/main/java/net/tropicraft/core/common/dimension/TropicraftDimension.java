package net.tropicraft.core.common.dimension;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.tropicraft.Constants;
import net.tropicraft.core.common.dimension.biome.TropicraftBiomeProvider;
import net.tropicraft.core.common.dimension.chunk.TropicraftChunkGenerator;

import java.util.function.Supplier;

public class TropicraftDimension {
	public static final ResourceLocation ID = new ResourceLocation(Constants.MODID, "tropics");

	public static final RegistryKey<World> WORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, ID);
	public static final RegistryKey<Dimension> DIMENSION = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, ID);
    public static final RegistryKey<DimensionType> DIMENSION_TYPE = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, ID);
    public static final RegistryKey<DimensionSettings> DIMENSION_SETTINGS = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, ID);

    public static ChunkGenerator createGenerator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettingsRegistry, long seed) {
        Supplier<DimensionSettings> dimensionSettings = () -> {
        	// fallback to overworld so that we don't crash before our datapack is loaded (horrible workaround)
			DimensionSettings settings = dimensionSettingsRegistry.get(DIMENSION_SETTINGS);
			return settings != null ? settings : dimensionSettingsRegistry.getOrThrow(DimensionSettings.OVERWORLD);
		};
        TropicraftBiomeProvider biomeSource = new TropicraftBiomeProvider(seed, biomeRegistry);
        return new TropicraftChunkGenerator(biomeSource, seed, dimensionSettings);
    }

	public static void teleportPlayer(ServerPlayerEntity player, RegistryKey<World> dimensionType) {
		if (player.level.dimension() == dimensionType) {
			teleportPlayerNoPortal(player, World.OVERWORLD);
		} else {
			teleportPlayerNoPortal(player, dimensionType);
		}
	}

	/**
	 * Finds the top Y position relative to the dimension the player is teleporting to and places
	 * the entity at that position. Avoids portal generation by using player.teleport() instead of
	 * player.changeDimension()
	 *
	 * @param player The player that will be teleported
	 * @param destination The target dimension to teleport to
	 */
	public static void teleportPlayerNoPortal(ServerPlayerEntity player, RegistryKey<World> destination) {
		if (!ForgeHooks.onTravelToDimension(player, destination)) return;

		ServerWorld world = player.server.getLevel(destination);

		int x = MathHelper.floor(player.getX());
		int z = MathHelper.floor(player.getZ());

		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		int topY = chunk.getHeight(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15);
		player.teleportTo(world, x + 0.5, topY + 1.0, z + 0.5, player.yRot, player.xRot);

		BasicEventHooks.firePlayerChangedDimensionEvent(player, destination, destination);
	}
}
