package net.tropicraft.core.common.entity.placeable;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.tropicraft.core.common.item.TropicraftItems;

public class BeachFloatEntity extends FurnitureEntity implements IEntityAdditionalSpawnData {

    @Nonnull
    private static final Random rand = new Random(298457L);
    @Nonnull
    private static final PerlinNoiseGenerator windNoise = new PerlinNoiseGenerator(rand, 1);

    /* Wind */
    private double windModifier = 0;

    /* Is any entity laying on the float? */
    public boolean isEmpty;

    /* Interpolation values */
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;

    /* Acceleration */
    public float rotationSpeed;

    /* Water checks */
    private double prevMotionY;

    public BeachFloatEntity(EntityType<BeachFloatEntity> type, World worldIn) {
        super(type, worldIn, TropicraftItems.BEACH_FLOATS);
        this.ignoreFrustumCheck = true;
        this.isEmpty = true;
        this.preventEntitySpawning = true;
        this.entityCollisionReduction = .95F;
        setEntityId(this.getEntityId());
    }
    
    public void setRotation(float yaw) {
        this.lerpYaw = this.rotationYaw = yaw;
    }

    @Override
    public void setEntityId(int id) {
        super.setEntityId(id);
        rand.setSeed(id);
        this.windModifier = (1 + (rand.nextGaussian() * 0.1)) - 0.05;
    }

    @Override
    public void tick() {
        Entity rider = getControllingPassenger();
        if (world.isRemote && rider instanceof PlayerEntity) {
            PlayerEntity controller = (PlayerEntity) rider;
            float move = controller.moveForward;
            float rot = -controller.moveStrafing;
            rotationSpeed += rot * 0.25f;

            float ang = rotationYaw;
            float moveX = MathHelper.sin(-ang * 0.017453292F) * move * 0.0035f;
            float moveZ = MathHelper.cos(ang * 0.017453292F) * move * 0.0035f;
            setMotion(getMotion().add(moveX, 0, moveZ));
        }

        if (this.inWater) {
            double windAng = (windNoise.getValue(posX / 1000, posZ / 1000) + 1) * Math.PI;
            double windX = Math.sin(windAng) * 0.0005 * windModifier;
            double windZ = Math.cos(windAng) * 0.0005 * windModifier;
            setMotion(getMotion().add(windX, 0, windZ));
            // Rotate towards a target yaw with some random perturbance
            double targetYaw = Math.toDegrees(windAng) + ((windModifier - 1) * 45);
            double yaw = (MathHelper.wrapDegrees(this.rotationYaw) + 180 - 35) % 360;
            double angleDiff = targetYaw - yaw;
            if (angleDiff > 0) {
                this.rotationSpeed += Math.min(0.005 * windModifier, angleDiff);
            } else {
                this.rotationSpeed += Math.max(-0.005 * windModifier, angleDiff);
            }
        }

        double water = getWaterLevel();
        double center = getCenterY();
        double eps = 1 / 16D;
        if (water < center - eps) { // Gravity
            setMotion(getMotion().add(0, -MathHelper.clamp(center - water, 0, 0.04), 0));
        } else if (water > center + eps) {
            double floatpush = MathHelper.clamp(water - center, 0, 0.02);
            setMotion(getMotion().add(0, floatpush, 0));
        } else if (Math.abs(getMotion().y) < 0.02) { // Close enough, just force to the correct spot
            if (getMotion().y != 0) {
                lerpY = water - 0.011;
            }
            setMotion(getMotion().mul(1, 0, 1));
            prevMotionY = 0;
        }
        
        super.tick();
        tickLerp();

        rotationYaw += rotationSpeed;
        move(MoverType.PLAYER, getMotion());

        setMotion(getMotion().mul(0.9, 0.9, 0.9));
        rotationSpeed *= 0.9f;

        if (!this.world.isRemote) {
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

            if (list != null && !list.isEmpty()) {
                for (int k1 = 0; k1 < list.size(); ++k1) {
                    Entity entity = list.get(k1);
                    if (entity != this.getControllingPassenger() && entity.canBePushed()) {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            if (this.getControllingPassenger() != null && !this.getControllingPassenger().isAlive()) {
                this.removePassengers();
            }
        }
    }
    
    @Override
    protected boolean preventMotion() {
        return false;
    }
    
    private double getCenterY() {
        AxisAlignedBB bb = getBoundingBox();
        return bb.minY + (bb.maxY - bb.minY) * 0.5D;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        this.prevMotionY = this.getMotion().y;
        super.updateFallState(y, onGroundIn, state, pos);
    }

    @Override
    public boolean handleWaterMovement() {
        AxisAlignedBB temp = getBoundingBox();
        setBoundingBox(temp.contract(1, 0, 1).contract(-1, 0.125, -1));
        if (this.handleFluidAcceleration(FluidTags.WATER)) {
            if (!this.inWater && !this.firstUpdate) {
                this.doWaterSplashEffect();
            }

            this.inWater = true;
        } else {
            this.inWater = false;
        }
        setBoundingBox(temp);
        return this.inWater;
    }
    
    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand) {
        if (!this.world.isRemote && !player.isSneaking()) {
            player.startRiding(this);
            return true;
        }

        return !player.isRidingSameEntity(this);
    }

    /* Following two methods mostly copied from EntityBoat interpolation code */
    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        if (teleport) {
            super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, teleport);
        } else {
            this.lerpX = x;
            this.lerpY = y;
            this.lerpZ = z;
            // Avoid "jumping" back to the client's rotation due to vanilla's dumb incomplete packets
            if (yaw != rotationYaw) {
                this.lerpYaw = (double) yaw;
            }
            this.lerpSteps = 10;
            this.rotationPitch = pitch;
        }
    }

    private void tickLerp() {
        if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
            double d0 = this.posX + (this.lerpX - this.posX) / (double) this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / (double) this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / (double) this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double) this.rotationYaw);
            this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    /* Following three methods copied from EntityBoat for passenger updates */

    @Override
    public void updatePassenger(@Nonnull Entity passenger) {
        if (this.isPassenger(passenger)) {
            // float yaw = this.rotationYaw;

            // passenger.setPosition(x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), z);

            float f = 0.0F;
            float f1 = (float) ((!isAlive() ? 0.001 : this.getMountedYOffset()) + passenger.getYOffset());

            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(passenger);

                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (passenger instanceof LivingEntity) {
                    f = (float) ((double) f + 0.2D);
                }
            }

            float len = 0.6f;
            double x = this.posX + (-MathHelper.sin(-this.rotationYaw * 0.017453292F) * len);
            double z = this.posZ + (-MathHelper.cos(this.rotationYaw * 0.017453292F) * len);
            passenger.setPosition(x, this.posY + (double) f1, z);
            passenger.rotationYaw += this.rotationSpeed;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + this.rotationSpeed);
            this.applyYawToEntity(passenger);

            if (passenger instanceof LivingEntity && this.getPassengers().size() > 1) {
                int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
                passenger.setRenderYawOffset(((LivingEntity) passenger).renderYawOffset + (float) j);
                passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) j);
            }

            if (passenger instanceof PlayerEntity) {
                ((PlayerEntity) passenger).setBoundingBox(getBoundingBox().expand(0, 0.3, 0).contract(0, -0.1875, 0));
            }
        }
    }
    
    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (passenger instanceof PlayerEntity) {
            passenger.recalculateSize();
        }
    }

    protected void applyYawToEntity(Entity entityToUpdate) {
        if (!entityToUpdate.world.isRemote || isClientFirstPerson()) {
            entityToUpdate.setRenderYawOffset(this.rotationYaw);
            float yaw = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
            float pitch = MathHelper.wrapDegrees(entityToUpdate.rotationPitch - this.rotationPitch);
            float clampedYaw = MathHelper.clamp(yaw, -105.0F, 105.0F);
            float clampedPitch = MathHelper.clamp(pitch, -100F, -10F);
            entityToUpdate.prevRotationYaw += clampedYaw - yaw;
            entityToUpdate.rotationYaw += clampedYaw - yaw;
            entityToUpdate.prevRotationPitch += clampedPitch - pitch;
            entityToUpdate.rotationPitch += clampedPitch - pitch;
            entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
        }
    }

    @Override
    public void applyOrientationToEntity(@Nonnull Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

    private boolean isClientFirstPerson() {
        return Minecraft.getInstance().gameSettings.thirdPersonView == 0;
    }

    /* Again, from entity boat, for water checks */

    private float getWaterLevel() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int minX = MathHelper.floor(axisalignedbb.minX);
        int maxX = MathHelper.ceil(axisalignedbb.maxX);
        int minY = MathHelper.floor(axisalignedbb.minY - prevMotionY);
        int maxY = minY + 1;
        int minZ = MathHelper.floor(axisalignedbb.minZ);
        int maxZ = MathHelper.ceil(axisalignedbb.maxZ);
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();

        try {
            float waterHeight = minY - 1;

            for (int y = maxY; y >= minY; --y) {
                for (int x = minX; x < maxX; x++) {
                    for (int z = minZ; z < maxZ; ++z) {
                        pos.setPos(x, y, z);
                        IFluidState fluidstate = this.world.getFluidState(pos);

                        if (fluidstate.getFluid().isEquivalentTo(Fluids.WATER)) {
                            waterHeight = Math.max(waterHeight, pos.getY() + fluidstate.getActualHeight(this.world, pos));
                        }
                        if (waterHeight >= maxY) {
                            return waterHeight;
                        }
                    }
                }
            }

            return waterHeight;
        } finally {
            pos.close();
        }
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public double getYOffset() {
        return 0;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @Override
    public double getMountedYOffset() {
        return getHeight() - 1.1;
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example, Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : (Entity) list.get(0);
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
     */
    @Override
    public Direction getAdjustedHorizontalFacing() {
        return this.getHorizontalFacing().rotateY();
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeDouble(this.lerpYaw);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.lerpYaw = additionalData.readDouble();
    }
}