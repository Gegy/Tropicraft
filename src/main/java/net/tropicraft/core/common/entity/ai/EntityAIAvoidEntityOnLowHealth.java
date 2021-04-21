package net.tropicraft.core.common.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class EntityAIAvoidEntityOnLowHealth<T extends Entity> extends Goal {
    private final Predicate<Entity> canBeSeenSelector;
    /** The entity we are attached to */
    protected CreatureEntity theEntity;
    private final double farSpeed;
    private final double nearSpeed;
    protected T closestLivingEntity;
    private final float avoidDistance;
    /** The PathEntity of our entity */
    private Path entityPathEntity;
    /** The PathNavigate of our entity */
    private final PathNavigator entityPathNavigate;
    /** Class of entity this behavior seeks to avoid */
    private final Class<T> classToAvoid;
    private final Predicate<Entity> avoidTargetSelector;
    private float healthToAvoid = 0F;

    public EntityAIAvoidEntityOnLowHealth(CreatureEntity theEntityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn, float healthToAvoid)
    {
        this(theEntityIn, classToAvoidIn, (p_203782_0_) -> true, avoidDistanceIn, farSpeedIn, nearSpeedIn, healthToAvoid);
    }

    public EntityAIAvoidEntityOnLowHealth(CreatureEntity theEntityIn, Class<T> classToAvoidIn, Predicate<Entity> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn, float healthToAvoid)
    {
        this.canBeSeenSelector = entity -> entity.isAlive() && theEntity.getSensing().canSee(entity);
        this.theEntity = theEntityIn;
        this.classToAvoid = classToAvoidIn;
        this.avoidTargetSelector = avoidTargetSelectorIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.entityPathNavigate = theEntityIn.getNavigation();
        this.healthToAvoid = healthToAvoid;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean canUse()
    {

        if (this.theEntity.getHealth() > healthToAvoid) return false;

        List<T> list = this.theEntity.level.getEntitiesOfClass(this.classToAvoid,
                this.theEntity.getBoundingBox().expandTowards((double)this.avoidDistance, 3.0D, (double)this.avoidDistance),
                EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(this.canBeSeenSelector).and(this.avoidTargetSelector)
        );

        if (list.isEmpty()) {
            return false;
        } else {
            this.closestLivingEntity = list.get(0);
            Vector3d Vector3d = RandomPositionGenerator.getPosAvoid(this.theEntity, 16, 7, new Vector3d(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ()));

            if (Vector3d == null) {
                return false;
            } else if (this.closestLivingEntity.distanceToSqr(Vector3d.x, Vector3d.y, Vector3d.z) < this.closestLivingEntity.distanceToSqr(this.theEntity)) {
                return false;
            } else {
                this.entityPathEntity = this.entityPathNavigate.createPath(Vector3d.x, Vector3d.y, Vector3d.z, 0);
                return this.entityPathEntity != null;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse()
    {
        return this.entityPathNavigate.isInProgress();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start()
    {
        this.entityPathNavigate.moveTo(this.entityPathEntity, this.farSpeed);
    }

    /**
     * Resets the task
     */
    @Override
    public void stop()
    {
        this.closestLivingEntity = null;
    }

    /**
     * Updates the task
     */
    @Override
    public void tick()
    {
        if (this.theEntity.distanceToSqr(this.closestLivingEntity) < 49.0D)
        {
            this.theEntity.getNavigation().setSpeedModifier(this.nearSpeed);
        }
        else
        {
            this.theEntity.getNavigation().setSpeedModifier(this.farSpeed);
        }
    }
}


