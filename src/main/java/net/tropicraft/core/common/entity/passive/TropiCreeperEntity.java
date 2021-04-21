package net.tropicraft.core.common.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.tropicraft.core.common.TropicraftTags;
import net.tropicraft.core.common.entity.ai.TropiCreeperSwellGoal;
import net.tropicraft.core.common.item.TropicraftItems;

import java.util.Collection;

public class TropiCreeperEntity extends CreatureEntity {
    private static final DataParameter<Integer> STATE = EntityDataManager.defineId(CreeperEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> IGNITED = EntityDataManager.defineId(CreeperEntity.class, DataSerializers.BOOLEAN);

    private int prevTimeSinceIgnited, timeSinceIgnited;
    private int fuseTime = 30;
    private int explosionRadius = 3;

    public TropiCreeperEntity(final EntityType<? extends CreatureEntity> entityType, final World p_i48575_2_) {
        super(entityType, p_i48575_2_);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return CreatureEntity.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new TropiCreeperSwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, OcelotEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, CatEntity.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, -1);
        this.entityData.define(IGNITED, false);
    }

    /**
     * The maximum height from where the entity is alowed to jump (used in pathfinder)
     */
    @Override
    public int getMaxFallDistance() {
        return this.getTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        boolean fall = super.causeFallDamage(distance, damageMultiplier);
        this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + distance * 1.5F);
        if (this.timeSinceIgnited > this.fuseTime - 5) {
            this.timeSinceIgnited = this.fuseTime - 5;
        }

        return fall;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putShort("Fuse", (short)this.fuseTime);
        compound.putByte("ExplosionRadius", (byte)this.explosionRadius);
        compound.putBoolean("ignited", this.hasIgnited());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Fuse", 99)) {
            this.fuseTime = compound.getShort("Fuse");
        }

        if (compound.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compound.getByte("ExplosionRadius");
        }

        if (compound.getBoolean("ignited")) {
            this.ignite();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        if (this.isAlive()) {
            this.prevTimeSinceIgnited = this.timeSinceIgnited;
            if (this.hasIgnited()) {
                this.setCreeperState(1);
            }

            int i = this.getCreeperState();
            if (i > 0 && this.timeSinceIgnited == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
            }

            this.timeSinceIgnited += i;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= this.fuseTime) {
                this.timeSinceIgnited = this.fuseTime;
                this.explode();
            }
        }

        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    /**
     * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
     */
    public int getCreeperState() {
        return this.entityData.get(STATE);
    }

    /**
     * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
     */
    public void setCreeperState(int state) {
        this.entityData.set(STATE, state);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
            this.level.playSound(player, getX(), getY(), getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            player.swing(hand);
            if (!this.level.isClientSide) {
                this.ignite();
                itemstack.hurtAndBreak(1, player, (p_213625_1_) -> {
                    p_213625_1_.broadcastBreakEvent(hand);
                });
                return ActionResultType.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    /**
     * Creates an explosion as determined by this creeper's power and explosion radius.
     */
    private void explode() {
        if (!this.level.isClientSide) {
            this.dead = true;
            //this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius, Explosion.Mode.NONE);
            //TODO: readd coconut bomb drop for creeper
            // this.dropItem(TCItemRegistry.coconutBomb.itemID, rand.nextInt(3) + 1);
            int radius = 5;
            int radiusSq = radius * radius;
            BlockPos center = blockPosition();
            for (int i = 0; i < 3 * radiusSq; i++) {
                BlockPos attempt = center.offset(random.nextInt((radius * 2) + 1) - radius, 0, random.nextInt((radius * 2) + 1) - radius);
                if (attempt.distSqr(center) < radiusSq) {
                    attempt = attempt.above(radius);
                    while (level.getBlockState(attempt).getMaterial().isReplaceable() && attempt.getY() > center.getY() - radius) {
                        attempt = attempt.below();
                    }
                    attempt = attempt.above();
                    BlockState state = TropicraftTags.Blocks.SMALL_FLOWERS.getRandomElement(random).defaultBlockState();
                    if (state.canSurvive(level, attempt)) {
                        level.setBlockAndUpdate(attempt, state);
                    }
                }
            }
            this.remove();
            this.spawnLingeringCloud();
        } else {
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 1F, getZ(), 1.0D, 0.0D, 0.0D);
        }
    }

    private void spawnLingeringCloud() {
        Collection<EffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(level, getX(), getY(), getZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());

            for(EffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
            }

            this.level.addFreshEntity(areaeffectcloudentity);
        }
    }

    public boolean hasIgnited() {
        return this.entityData.get(IGNITED);
    }

    public void ignite() {
        this.entityData.set(IGNITED, true);
    }
    
    public float getCreeperFlashIntensity(float partialTicks) {
       return MathHelper.lerp(partialTicks, (float)this.prevTimeSinceIgnited, (float)this.timeSinceIgnited) / (float)(this.fuseTime - 2);
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(TropicraftItems.TROPICREEPER_SPAWN_EGG.get());
    }
}
