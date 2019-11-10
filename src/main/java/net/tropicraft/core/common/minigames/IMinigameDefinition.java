package net.tropicraft.core.common.minigames;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * Used as a discriminant for a registered minigame. Defines the logic of the
 * minigame as it is actively running, and provides methods to customize the
 * ruleset for the minigame such as maximum and minimum participants, game types
 * for each player type, dimension the minigame takes place in, etc.
 */
public interface IMinigameDefinition
{
    /**
     * The identifier for this minigame definition. Must be unique
     * compared to other registered minigames.
     * @return The identifier for this minigame definition.
     */
    ResourceLocation getID();

    /**
     * Used within messages sent to players as the minigame starts, stops, etc.
     * @return The unlocalized key string for the name of this minigame.
     */
    String getUnlocalizedName();

    /**
     * The targeted dimension you'd like this minigame to teleport players to
     * when they join as players or spectators.
     * @return The dimension type players are teleported to when joining.
     */
    DimensionType getDimension();

    /**
     * Set when the minigame starts and you are a participant.
     * @return The game type players are set to when active participants of the minigame.
     */
    GameType getParticipantGameType();

    /**
     * Set when the minigame starts and you are a spectator.
     * @return The game type players are set to when they are considered spectators.
     */
    GameType getSpectatorGameType();

    /**
     * Relative to the dimension world specified by the dimension type.
     * @return The position spectators start at when the minigame starts.
     */
    BlockPos getSpectatorPosition();

    /**
     * Relative to the dimension world specified by the dimension type.
     * @param instance The instance of the running minigame.
     * @return The block position for players to respawn at on death.
     */
    BlockPos getPlayerRespawnPosition(IMinigameInstance instance);

    /**
     * Relative to the dimension world specified by the dimension type.
     * @return Block positions for each participant to spawn at when the
     * minigame starts. Each index specifies the participant number.
     *
     * Index 0 == Player 1's position.
     * Index 1 == Player 2's position.
     * etc...
     *
     * Make sure the length of this returned array is the same as the maximum
     * participant count defined in this minigame definition.
     */
    BlockPos[] getParticipantPositions();

    /**
     * Will not let you start the minigame without at least this amount of
     * players registered for the polling minigame.
     *
     * @return The minimum amount of players required to start the minigame.
     */
    int getMinimumParticipantCount();

    /**
     * Will only select up to this many participants to actually play
     * in the started minigame. The rest of the players registered for
     * the minigame will be slotted in as spectators where they can watch
     * the minigame unfold.
     *
     * @return The maximum amount of players that can be participants in the
     * minigame.
     */
    int getMaximumParticipantCount();

    /**
     * Helper method to define unique logic for the minigame as it is
     * running. Only called when a minigame using this definition is
     * actively running.
     *
     * @param world The world to run this for, currently a worldUpdate call happens per each loaded world
     * @param instance The instance of the currently running minigame.
     */
    default void worldUpdate(World world, IMinigameInstance instance) {}

    /**
     * Helper method to catch when a player dies while inside an active
     * minigame using this definition. Useful for unique logic defined
     * by this minigame definition.
     *
     * @param player The player which died.
     * @param instance The instance of the currently running minigame.
     */
    default void onPlayerDeath(ServerPlayerEntity player, IMinigameInstance instance) {}

    /**
     * Helper method to create unique logic for when the player updates.
     * @param player They player which is updating.
     * @param instance The instance of the currently running minigame.
     */
    default void onPlayerUpdate(ServerPlayerEntity player, IMinigameInstance instance) {}

    /**
     * Helper method to catch when a player respawns while inside an active
     * minigame using this definition. Useful for unique logic defined
     * by this minigame definition.
     *
     * @param player The player which died.
     * @param instance The instance of the currently running minigame.
     */
    default void onPlayerRespawn(ServerPlayerEntity player, IMinigameInstance instance) {}

    /**
     * For when a minigame finishes. Useful for cleanup related to this
     * minigame definition.
     *
     * @param commandSource Command source for the minigame instance.
     *                      Can be used to execute some commands for
     *                      the minigame from a datapack.
     */
    default void onFinish(CommandSource commandSource) {}

    /**
     * For when the minigame has finished and all players are teleported
     * out of the dimension.
     * @param commandSource Command source for the minigame instance.
     *                      Can be used to execute some commands for
     *                      the minigame from a datapack.
     */
     default void onPostFinish(CommandSource commandSource) {}

    /**
     * For when a minigame starts. Useful for preparing the minigame.
     *
     * @param commandSource Command source for the minigame instance.
     *                      Can be used to execute some commands for
     *                      the minigame from a datapack.
     */
    default void onStart(CommandSource commandSource) {}

    /**
     * For before a minigame starts. Useful for preparing the minigame.
     */
    default void onPreStart() {}
}
