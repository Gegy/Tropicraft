package net.tropicraft.core.common.config;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.ForgeConfigSpec.*;

@EventBusSubscriber
public class ConfigLT {

    private static final Builder CLIENT_BUILDER = new Builder();

    private static final Builder COMMON_BUILDER = new Builder();

    public static final CategoryGeneral GENERAL = new CategoryGeneral();

    public static final CategoryIslandRoyale MINIGAME_ISLAND_ROYALE = new CategoryIslandRoyale();

    public static final class CategoryIslandRoyale {

        public final IntValue minimumPlayerCount;
        public final IntValue maximumPlayerCount;

        public final ConfigValue<String> minigame_IslandRoyale_playerPositions;
        public final ConfigValue<String> minigame_IslandRoyale_respawnPosition;
        public final ConfigValue<String> minigame_IslandRoyale_spectatorPosition;

        public final IntValue phase1Length;
        public final IntValue phase2Length;
        public final IntValue phase3Length;

        public final IntValue phase2TargetWaterLevel;
        public final IntValue phase3TargetWaterLevel;

        public final DoubleValue rainHeavyChance;
        public final DoubleValue rainAcidChance;
        public final DoubleValue heatwaveChance;

        public final IntValue rainHeavyMinTime;
        public final IntValue rainHeavyExtraRandTime;
        public final IntValue rainAcidMinTime;
        public final IntValue rainAcidExtraRandTime;
        public final IntValue heatwaveMinTime;
        public final IntValue heatwaveExtraRandTime;

        public final DoubleValue heatwaveMovementMultiplier;

        public final IntValue acidRainDamage;
        public final IntValue acidRainDamageRate;

        private CategoryIslandRoyale() {
            COMMON_BUILDER.comment("Island Royale settings").push("island_royale");

            minigame_IslandRoyale_playerPositions = COMMON_BUILDER.comment("List of spawn positions for players, number of entries must match maximumPlayerCount config value, separate each position by ; and each x y and z with , example: 5780, 141, 6955; 5780, 141, 6955")
                    .define("minigame_IslandRoyale_playerPositions", "5780, 141, 6955; 5780, 141, 6955; " +
                            "5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; " +
                            "5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955; 5780, 141, 6955");

            minigame_IslandRoyale_respawnPosition = COMMON_BUILDER.define("minigame_IslandRoyale_respawnPosition", "5780, 141, 6955");
            minigame_IslandRoyale_spectatorPosition = COMMON_BUILDER.define("minigame_IslandRoyale_spectatorPosition", "5780, 141, 6955");

            minimumPlayerCount = COMMON_BUILDER.defineInRange("minimumPlayerCount", 3, 1, 255);
            maximumPlayerCount = COMMON_BUILDER.defineInRange("maximumPlayerCount", 16, 2, 255);

            phase1Length = COMMON_BUILDER.comment("Time in ticks first game phase will last").defineInRange("phase1Length", 20*60*5, 1, Integer.MAX_VALUE);
            phase2Length = COMMON_BUILDER.comment("Time in ticks second game phase will last").defineInRange("phase2Length", 20*60*5, 1, Integer.MAX_VALUE);
            phase3Length = COMMON_BUILDER.comment("Time in ticks third game phase will last").defineInRange("phase3Length", 20*60*5, 1, Integer.MAX_VALUE);

            phase2TargetWaterLevel = COMMON_BUILDER.comment("Target water level for second game phase").defineInRange("phase2TargetWaterLevel", 133, 1, Integer.MAX_VALUE);
            phase3TargetWaterLevel = COMMON_BUILDER.comment("Target water level for third game phase").defineInRange("phase3TargetWaterLevel", 150, 1, Integer.MAX_VALUE);

            rainHeavyChance = COMMON_BUILDER.comment("Tried every second, 0.01 = 1% chance, 1 = 100% chance").defineInRange("rainHeavyChance", 0.01, 0, 1D);
            rainAcidChance = COMMON_BUILDER.comment("Tried every second, 0.01 = 1% chance, 1 = 100% chance").defineInRange("rainAcidChance", 0.01, 0, 1D);
            heatwaveChance = COMMON_BUILDER.comment("Tried every second, 0.01 = 1% chance, 1 = 100% chance").defineInRange("heatwaveChance", 0.01, 0, 1D);

            rainHeavyMinTime = COMMON_BUILDER.defineInRange("rainHeavyMinTime", 20*60*2, 1, Integer.MAX_VALUE);
            rainHeavyExtraRandTime = COMMON_BUILDER.defineInRange("rainHeavyExtraRandTime", 20*60*2, 1, Integer.MAX_VALUE);
            rainAcidMinTime = COMMON_BUILDER.defineInRange("rainAcidMinTime", 20*60*2, 1, Integer.MAX_VALUE);
            rainAcidExtraRandTime = COMMON_BUILDER.defineInRange("rainAcidExtraRandTime", 20*60*2, 1, Integer.MAX_VALUE);
            heatwaveMinTime = COMMON_BUILDER.defineInRange("heatwaveMinTime", 20*60*2, 1, Integer.MAX_VALUE);
            heatwaveExtraRandTime = COMMON_BUILDER.defineInRange("heatwaveExtraRandTime", 20*60*2, 1, Integer.MAX_VALUE);

            heatwaveMovementMultiplier = COMMON_BUILDER.defineInRange("heatwaveMovementMultiplier", 0.5, 0.01, 1D);

            acidRainDamage = COMMON_BUILDER.defineInRange("acidRainDamage", 1, 1, Integer.MAX_VALUE);
            acidRainDamageRate = COMMON_BUILDER.comment("Rate in ticks, 20 = 1 second").defineInRange("acidRainDamageRate", 60, 1, Integer.MAX_VALUE);

            COMMON_BUILDER.pop();
        }
    }

    public static final class CategoryGeneral {

        public final DoubleValue Precipitation_Particle_effect_rate;

        public final BooleanValue UseCrouch;

        private CategoryGeneral() {
            CLIENT_BUILDER.comment("General mod settings").push("general");

            Precipitation_Particle_effect_rate = CLIENT_BUILDER
                    .defineInRange("Precipitation_Particle_effect_rate", 0.7D, 0D, 1D);

            UseCrouch = CLIENT_BUILDER.comment("Enable crawling anywhere by pressing the sprint key while holding down the sneak key")
                    .define("UseCrawl", true);

            CLIENT_BUILDER.pop();
        }
    }

    public static final ForgeConfigSpec CLIENT_CONFIG = CLIENT_BUILDER.build();

    public static final ForgeConfigSpec SERVER_CONFIG = COMMON_BUILDER.build();

    /**
     * values used during runtime that require processing from disk
     */
    public static BlockPos[] minigame_IslandRoyale_playerPositions = new BlockPos[] {
            new BlockPos(5780, 141, 6955) };

    public static BlockPos minigame_IslandRoyale_respawnPosition = new BlockPos(5780, 141, 6955);

    public static BlockPos minigame_IslandRoyale_spectatorPosition = new BlockPos(5780, 141, 6955);


    public static void onLoad(final ModConfig.Loading configEvent) {
        minigame_IslandRoyale_playerPositions = getAsBlockPosArray(ConfigLT.MINIGAME_ISLAND_ROYALE.minigame_IslandRoyale_playerPositions.get());
        minigame_IslandRoyale_respawnPosition = stringToBlockPos(ConfigLT.MINIGAME_ISLAND_ROYALE.minigame_IslandRoyale_respawnPosition.get());
        minigame_IslandRoyale_spectatorPosition = stringToBlockPos(ConfigLT.MINIGAME_ISLAND_ROYALE.minigame_IslandRoyale_spectatorPosition.get());

        for (BlockPos pos : minigame_IslandRoyale_playerPositions) System.out.println("RESULT: " + pos);
    }

    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        //System.out.println("file changed!" + configEvent.toString());
    }

    public static BlockPos stringToBlockPos(String posString) {
        try {
            String splitterInt = ",";
            String[] substr = posString.split(splitterInt);
            return new BlockPos(
                    Integer.valueOf(substr[0].trim()),
                    Integer.valueOf(substr[1].trim()),
                    Integer.valueOf(substr[2].trim()));
        } catch (Exception ex) {
            //in case of bad config, at least dont tp them out of world
            return BlockPos.ZERO.add(0, 255, 0);
        }

    }

    public static BlockPos[] getAsBlockPosArray(String string) {
        String splitterBlockPos = ";";
        String[] posStrings = string.split(splitterBlockPos);
        List<BlockPos> listPos = new ArrayList<>();

        for (String posString : posStrings) {
            try {
                listPos.add(stringToBlockPos(posString));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return listPos.stream().toArray(BlockPos[]::new);
    }
}
