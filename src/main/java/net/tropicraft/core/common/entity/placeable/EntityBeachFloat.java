package net.tropicraft.core.common.entity.placeable;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tropicraft.core.registry.ItemRegistry;

public class EntityBeachFloat extends EntityPlaceableColored {

	/** Is any entity laying on the float? */
	public boolean isEmpty;
	
	/** Interpolation values */
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;

	/** Acceleration */
	public float rotationSpeed;
	
	public EntityBeachFloat(World worldIn) {
		super(worldIn);
		setSize(2F, 0.175F);
		this.ignoreFrustumCheck = true;
		this.isEmpty = true;
		this.preventEntitySpawning = true;
		this.entityCollisionReduction = .95F;
	}

	public EntityBeachFloat(World world, double x, double y, double z, int color, EntityPlayer player) {
		this(world);
		setPosition(x, y, z);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		setColor(color);		
		rotationYaw = this.getAngleToPlayer(player);
	}
	
	@Override
	public void onEntityUpdate() {
	    super.onEntityUpdate();
	    tickLerp();

	    Entity rider = getControllingPassenger();
	    if (world.isRemote && rider instanceof EntityPlayer) {
	        EntityPlayer controller = (EntityPlayer) rider;
	        float move = controller.moveForward;
	        float rot = -controller.moveStrafing;
	        rotationSpeed += rot * 0.25f;
	        
	        float ang = rotationYaw;
            float moveX = MathHelper.sin(-ang * 0.017453292F) * move * 0.0035f;
            float moveZ = MathHelper.cos(ang * 0.017453292F) * move * 0.0035f;
            motionX += moveX;
            motionZ += moveZ;
	    }
	    
	    motionY = 0;
	    
	    rotationYaw += rotationSpeed;
	    move(MoverType.PLAYER, motionX, motionY, motionZ);
	    
	    motionX *= 0.9f;
	    motionZ *= 0.9f;
	    rotationSpeed *= 0.9f;

        if (!this.world.isRemote) {
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

            if (list != null && !list.isEmpty()) {
                for (int k1 = 0; k1 < list.size(); ++k1) {
                    Entity entity = list.get(k1);
                    if (entity != this.getControllingPassenger() && entity.canBePushed()) {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            if (this.getControllingPassenger() != null && this.getControllingPassenger().isDead) {
                this.removePassengers();
            }
        }
	}
	
    /** Following two methods mostly copied from EntityBoat interpolation code */

    @SideOnly(Side.CLIENT)
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
    
    /** Following three methods copied from EntityBoat for passenger updates */
    
    @Override
    public void updatePassenger(@Nonnull Entity passenger) {
        if (this.isPassenger(passenger)) {
            // float yaw = this.rotationYaw;

            // passenger.setPosition(x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), z);

            float f = 0.0F;
            float f1 = (float) ((this.isDead ? 0.001 : this.getMountedYOffset()) + passenger.getYOffset());

            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(passenger);

                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (passenger instanceof EntityAnimal) {
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

            if (passenger instanceof EntityAnimal && this.getPassengers().size() > 1) {
                int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
                passenger.setRenderYawOffset(((EntityAnimal) passenger).renderYawOffset + (float) j);
                passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) j);
            }
            
            if (passenger instanceof EntityPlayer) {
                ((EntityPlayer)passenger).setEntityBoundingBox(getEntityBoundingBox().expand(0, 0.3, 0));
            }
        }
    }

    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void applyOrientationToEntity(@Nonnull Entity entityToUpdate) {
        this.applyYawToEntity(entityToUpdate);
    }

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource damageSource, float par2) {
		if (this.isEntityInvulnerable(damageSource)) {
			return false;
		} else if (!this.world.isRemote && !this.isDead) {
			this.setForwardDirection(-this.getForwardDirection());
			this.setTimeSinceHit(10);
			this.setDamage(this.getDamage() + par2 * 10.0F);
			this.markVelocityChanged();
			boolean flag = damageSource.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)damageSource.getTrueSource()).capabilities.isCreativeMode;

			if (flag || this.getDamage() > 40.0F) {
				if (!flag) {
					this.entityDropItem(new ItemStack(ItemRegistry.beach_float, 1,  getDamageFromColor()), 0.0F);
				}

				this.setDead();
			}

			return true;
		} else {
			return true;
		}
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	@Override
	public boolean canBePushed() {
		return true;
	}

	/**
	 * Returns the Y offset from the entity's position for any entity riding this one.
	 */
	@Override
	public double getMountedYOffset() {
		return height - 1.25;
	}

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Override
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : (Entity)list.get(0);
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into
     * account.
     */
	@Override
    public EnumFacing getAdjustedHorizontalFacing() {
        return this.getHorizontalFacing().rotateY();
    }
	
	@Override
	public boolean shouldRiderSit() {
	    return false;
	}
}
