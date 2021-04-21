package net.tropicraft.core.common.block.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.tropicraft.core.common.block.DrinkMixerBlock;
import net.tropicraft.core.common.drinks.Drink;
import net.tropicraft.core.common.drinks.Drinks;
import net.tropicraft.core.common.drinks.Ingredient;
import net.tropicraft.core.common.drinks.MixerRecipes;
import net.tropicraft.core.common.item.CocktailItem;
import net.tropicraft.core.common.network.TropicraftPackets;
import net.tropicraft.core.common.network.message.MessageMixerInventory;
import net.tropicraft.core.common.network.message.MessageMixerStart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrinkMixerTileEntity extends TileEntity implements ITickableTileEntity, IMachineTile {
	/** Number of ticks to mix */
	private static final int TICKS_TO_MIX = 4*20;
	private static final int MAX_NUM_INGREDIENTS = 3;

	/** Number of ticks the mixer has been mixin' */
	private int ticks;
	public NonNullList<ItemStack> ingredients;
	private boolean mixing;
	public ItemStack result = ItemStack.EMPTY;

	public DrinkMixerTileEntity() {
		super(TropicraftTileEntityTypes.DRINK_MIXER.get());
		mixing = false;
		ingredients = NonNullList.withSize(MAX_NUM_INGREDIENTS, ItemStack.EMPTY);
	}

	@Override
	public void load(BlockState blockState, @Nonnull CompoundNBT nbt) {
		super.load(blockState, nbt);
		ticks = nbt.getInt("MixTicks");
		mixing = nbt.getBoolean("Mixing");

		for (int i = 0; i < MAX_NUM_INGREDIENTS; i++) {
			if (nbt.contains("Ingredient" + i)) {
				ingredients.set(i, ItemStack.of(nbt.getCompound("Ingredient" + i)));
			}
		}

		if (nbt.contains("Result")) {
			result = ItemStack.of(nbt.getCompound("Result"));
		}
	}

	@Override
	public @Nonnull CompoundNBT save(@Nonnull CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("MixTicks", ticks);
		nbt.putBoolean("Mixing", mixing);

		for (int i = 0; i < MAX_NUM_INGREDIENTS; i++) {
			CompoundNBT ingredientNbt = new CompoundNBT();
	        ingredients.get(i).save(ingredientNbt);
	        nbt.put("Ingredient" + i, ingredientNbt);
		}

		CompoundNBT resultNbt = new CompoundNBT();
		result.save(resultNbt);
		nbt.put("Result", resultNbt);

		return nbt;
	}

	@Override
	public void tick() {
		if (ticks < TICKS_TO_MIX && mixing) {
			ticks++;
			if (ticks == TICKS_TO_MIX) {
				finishMixing();
			}
		}
	}

	public boolean isDoneMixing() {
		return !result.isEmpty();
	}

	public NonNullList<ItemStack> getIngredients() {
		return this.ingredients;
	}
	
	public static List<Ingredient> listIngredients(@Nonnull ItemStack stack) {
		List<Ingredient> is = new ArrayList<>();

		if (Drink.isDrink(stack.getItem())) {
			Collections.addAll(is, CocktailItem.getIngredients(stack));
		} else {
			final Ingredient i = Ingredient.findMatchingIngredient(stack);
			if (i != null) {
			    is.add(i);
			}
		}

		return is;
	}

	public void startMixing() {
		this.ticks = 0;
		this.mixing = true;
		if (!level.isClientSide) {
			TropicraftPackets.sendToDimension(new MessageMixerStart(this), level.dimension());
		}
	}
	
	private void dropItem(@Nonnull ItemStack stack, @Nullable PlayerEntity at) {
		if (at == null) {
			BlockPos pos = getBlockPos().relative(getBlockState().getValue(DrinkMixerBlock.FACING));
			InventoryHelper.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
		} else {
			InventoryHelper.dropItemStack(level, at.getX(), at.getY(), at.getZ(), stack);
		}
	}

	public void emptyMixer(@Nullable PlayerEntity at) {
		for (int i = 0; i < MAX_NUM_INGREDIENTS; i++) {
			if (!ingredients.get(i).isEmpty()) {
				dropItem(ingredients.get(i), at);
				ingredients.set(i, ItemStack.EMPTY);
			}
		}

		ticks = TICKS_TO_MIX;
		mixing = false;
		syncInventory();
	}

	public void retrieveResult(@Nullable PlayerEntity at) {
	    if (result.isEmpty()) {
	        return;
	    }
	    
		dropItem(result, at);

		for (int i = 0; i < MAX_NUM_INGREDIENTS; i++) {
			// If we're not using one of the ingredient slots, just move along
			if (ingredients.get(i).isEmpty()) {
				continue;
			}

			final ItemStack container = ingredients.get(i).getItem().getContainerItem(ingredients.get(i));

			if (!container.isEmpty()) {
				dropItem(container, at);
			}
		}

		ingredients.clear();
		result = ItemStack.EMPTY;
		syncInventory();
	}

	public void finishMixing() {
		result = getResult(getIngredients());
		mixing = false;
		ticks = 0;
		syncInventory();
	}

	public boolean addToMixer(@Nonnull ItemStack ingredient) {
		if (ingredients.get(0).isEmpty()) {
			if (!Drink.isDrink(ingredient.getItem())) {
				Ingredient i = Ingredient.findMatchingIngredient(ingredient);
				// Ordinarily we check for primary here, but I don't think that feature
				// is as relevant anymore. Will leave it here just in case!
				if (i == null/* || !i.isPrimary()*/) {
					return false;
				}
			}
			ingredients.set(0, ingredient);
			syncInventory();
			return true;
		} else if (ingredients.get(1).isEmpty()) {
			if (Drink.isDrink(ingredient.getItem())) {
				// prevent mixing multiple primary ingredients
				// all cocktails already contain one
				return false;
			}

			Ingredient ing0 = Ingredient.findMatchingIngredient(ingredients.get(0));
			Ingredient i = Ingredient.findMatchingIngredient(ingredient);

			// See above comment about isPrimary()
			if (i == null/* || i.isPrimary()*/ || ing0.id == i.id) {
				return false;
			}

			ingredients.set(1, ingredient);
			syncInventory();
			return true;
		} else if (ingredients.get(2).isEmpty()) {
			if (Drink.isDrink(ingredient.getItem())) {
				// prevent mixing multiple primary ingredients
				// all cocktails already contain one
				return false;
			}

			Ingredient ing0 = Ingredient.findMatchingIngredient(ingredients.get(0));
			Ingredient ing1 = Ingredient.findMatchingIngredient(ingredients.get(1));
			Ingredient i = Ingredient.findMatchingIngredient(ingredient);

			// See above comment about isPrimary()
			if (i == null/* || i.isPrimary()*/ || ing0.id == i.id || ing1.id == i.id) {
				return false;
			}

			ingredients.set(2, ingredient);
			syncInventory();
			return true;
		} else {
			return false;
		}
	}

	public boolean isMixing() {
		return mixing;
	}

	private boolean isMixerFull() {
		return MixerRecipes.isValidRecipe(ingredients);
		//return ingredients[0] != null && ingredients[1] != null;
	}

	public boolean canMix() {
		return !mixing && isMixerFull();
	}
	
	/* == IMachineTile == */
	
	@Override
	public boolean isActive() {
	    return isMixing();
	}
	
	@Override
	public float getProgress(float partialTicks) {
	    return (ticks + partialTicks) / TICKS_TO_MIX;
	}
	
	@Override
	public Direction getDirection(BlockState state) {
	    return state.getValue(DrinkMixerBlock.FACING);
	}

	/**
	 * Called when you receive a TileEntityData packet for the location this
	 * TileEntity is currently in. On the client, the NetworkManager will always
	 * be the remote server. On the server, it will be whomever is responsible for
	 * sending the packet.
	 *
	 * @param net The NetworkManager the packet originated from
	 * @param pkt The data packet
	 */
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		load(getBlockState(), pkt.getTag());
	}

	protected void syncInventory() {
		if (!level.isClientSide) {
			TropicraftPackets.sendToDimension(new MessageMixerInventory(this), level.dimension());
		}
	}

	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 1, this.getUpdateTag());
	}

	public CompoundNBT getUpdateTag() {
		return writeItems(new CompoundNBT());
	}

	private CompoundNBT writeItems(final CompoundNBT nbt) {
		super.save(nbt);
		ItemStackHelper.saveAllItems(nbt, ingredients, true);
		ItemStackHelper.saveAllItems(nbt, NonNullList.of(result), true);
		return nbt;
	}

	public ItemStack getResult(NonNullList<ItemStack> ingredients2) {
		return Drinks.getResult(ingredients2);
	}
}
