package net.tropicraft.core.common.entity.egg;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

public abstract class EggEntity extends LivingEntity {

    private static final DataParameter<Integer> HATCH_DELAY = EntityDataManager.createKey(EggEntity.class, DataSerializers.VARINT);

    public double rotationRand;
   
    public EggEntity(final EntityType<? extends EggEntity> type, World w) {
        super(type, w);
        rotationRand = 0;
        ignoreFrustumCheck = true;
       
        rotationYaw = rand.nextInt(360);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return LivingEntity.registerAttributes().createMutableAttribute(Attributes.MAX_HEALTH, 2.0);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        ticksExisted = compound.getInt("ticks");
        setHatchDelay(compound.getInt("hatchDelay"));
        super.readAdditional(compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putInt("ticks", ticksExisted);
        compound.putInt("hatchDelay", getHatchDelay());
        super.writeAdditional(compound);
    }

    @Override
    protected void registerData() {
        super.registerData();
        dataManager.register(HATCH_DELAY, 0);
        setHatchDelay(-60 + rand.nextInt(120));
    }

    public abstract boolean shouldEggRenderFlat();
    
    public abstract String getEggTexture();
    
    /**
     * Create and return a Entity here
     */
    public abstract Entity onHatch();
    
    /**
     * The amount of time in ticks it will take for the egg to hatch
     *     eg. hatch on tick n
     * @return a positive number
     */
    public abstract int getHatchTime();
    
    /**
     * The amount of time in ticks the egg will move around before it hatches
     *  eg. start rolling n ticks before hatch
     * @return a positive number lower than getHatchTime()
     */
    public abstract int getPreHatchMovement();
    
    public int getRandomHatchDelay() {
        return this.getDataManager().get(HATCH_DELAY);
    }
     
    public boolean isHatching() {
        return this.ticksExisted > (getHatchTime() + getRandomHatchDelay());
    }
    
    public boolean isNearHatching() {
        return this.ticksExisted > (getHatchTime() + getRandomHatchDelay()) - getPreHatchMovement();
    }

    @Override
    public void livingTick() {
        super.livingTick();
        
        if (isNearHatching()) {
            rotationRand += 0.1707F * world.rand.nextFloat();
            
            // Hatch time!
            if (ticksExisted >= this.getHatchTime()) {
                if (!world.isRemote) {
                    final Entity ent = onHatch();
                    ent.setLocationAndAngles(getPosX(), getPosY(), getPosZ(), 0.0F, 0.0F);
                    world.addEntity(ent);
                    remove();
                }
            }
        } 
    }
    
    public void setHatchDelay(int i) {
        this.getDataManager().set(HATCH_DELAY, -60 + rand.nextInt(120));
    }
    
    public int getHatchDelay() {
        return this.getDataManager().get(HATCH_DELAY);
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
    }

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.LEFT;
    }
}
