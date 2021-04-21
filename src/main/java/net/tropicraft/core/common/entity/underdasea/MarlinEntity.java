package net.tropicraft.core.common.entity.underdasea;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.tropicraft.core.common.item.TropicraftItems;

import javax.annotation.Nullable;

public class MarlinEntity extends AbstractFishEntity {

    private static final DataParameter<String> TEXTURE_NAME = EntityDataManager.createKey(MarlinEntity.class, DataSerializers.STRING);

    public MarlinEntity(EntityType<? extends AbstractFishEntity> type, World world) {
        super(type, world);
        experienceValue = 5;
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(TEXTURE_NAME, "marlin");
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractFishEntity.func_234176_m_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 5.0);
    }
    
	@Override
    protected ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        return ActionResultType.PASS;
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficultyInstance, SpawnReason spawnReason, @Nullable ILivingEntityData entityData, @Nullable CompoundNBT nbt) {
        setTexture(rand.nextInt(50) == 0 ? "purple_marlin" : "marlin");
        return super.onInitialSpawn(world, difficultyInstance, spawnReason, entityData, nbt);
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putString("Texture", getTexture());
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        setTexture(nbt.getString("Texture"));
    }

    @Override
    protected ItemStack getFishBucket() {
        return ItemStack.EMPTY;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return null;
    }

    public void setTexture(final String textureName) {
        getDataManager().set(TEXTURE_NAME, textureName);
    }

    public String getTexture() {
        return getDataManager().get(TEXTURE_NAME);
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(TropicraftItems.MARLIN_SPAWN_EGG.get());
    }
}
