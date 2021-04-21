package net.tropicraft.core.common.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.tropicraft.core.common.entity.TropicraftEntities;
import net.tropicraft.core.common.item.TropicraftItems;

import javax.annotation.Nullable;
import java.util.*;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class FailgullEntity extends AnimalEntity implements IFlyingAnimal {

	private boolean isFlockLeader;
	private static final DataParameter<Optional<UUID>> FLOCK_LEADER_UUID = EntityDataManager.createKey(FailgullEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public FailgullEntity(EntityType<? extends FailgullEntity> type, World world) {
		super(type, world);
		experienceValue = 1;
		moveController = new FlyingMovementController(this, 5, true);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.COCOA, -1.0F);
		this.setPathPriority(PathNodeType.FENCE, -1.0F);
	}

	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return AnimalEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 3.0)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.6)
				.createMutableAttribute(Attributes.FLYING_SPEED, 0.9)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 12.0);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(FLOCK_LEADER_UUID, Optional.empty());
	}

	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		isFlockLeader = nbt.getBoolean("IsFlockLeader");
		if (nbt.contains("FlockLeader")) {
			setFlockLeader(Optional.of(nbt.getUniqueId("FlockLeader")));
		} else {
			setFlockLeader(Optional.empty());
		}
	}

	@Override
	public void writeAdditional(final CompoundNBT nbt) {
		super.writeAdditional(nbt);
		nbt.putBoolean("IsFlockLeader", isFlockLeader);
		dataManager.get(FLOCK_LEADER_UUID).ifPresent(uuid -> nbt.putUniqueId("FlockLeader", uuid));
	}

	@Override
	public float getBlockPathWeight(final BlockPos pos, final IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}

	@Override
	public void registerGoals() {
		goalSelector.addGoal(0, new ValidateFlockLeader(this));
		goalSelector.addGoal(1, new SelectFlockLeader(this));
		goalSelector.addGoal(2, new SetTravelDestination());
		goalSelector.addGoal(2, new FollowLeaderGoal());
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return sizeIn.height * 0.5F;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	@Override
	protected boolean makeFlySound() {
		return false;
	}

	@Override
	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir();
			}
		};
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		return flyingpathnavigator;
	}

	private void poop() {
		if (!world.isRemote && world.rand.nextInt(20) == 0) {
			SnowballEntity s = new SnowballEntity(world, getPosX(), getPosY(), getPosZ());
			s.shoot(0, 0, 0, 0, 0);
			world.addEntity(s);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Nullable
	@Override
	public AgeableEntity createChild(ServerWorld world, AgeableEntity partner) {
		return null;
	}

	private void setIsFlockLeader(final boolean isFlockLeader) {
		this.isFlockLeader = isFlockLeader;
	}

	private void setFlockLeader(final Optional<UUID> flockLeaderUUID) {
		dataManager.set(FLOCK_LEADER_UUID, flockLeaderUUID);
	}

	private boolean getIsFlockLeader() {
		return isFlockLeader;
	}

	private boolean hasFlockLeader() {
		return dataManager.get(FLOCK_LEADER_UUID).isPresent();
	}

	@Nullable
	private Entity getFlockLeader() {
		if (world instanceof ServerWorld && hasFlockLeader()) {
			return ((ServerWorld) world).getEntityByUuid(dataManager.get(FLOCK_LEADER_UUID).get());
		}

		return null;
	}

	@Nullable
	private BlockPos getRandomLocation() {
		final Random random = getRNG();
		for (int i = 0; i < 20; i++) {
			double nextXPos = getPosX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 48);
			double nextYPos = getPosY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 3);
			double nextZPos = getPosZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 48);
			final BlockPos pos = new BlockPos(nextXPos, nextYPos, nextZPos);
			if (world.isAirBlock(pos)) {
				return pos;
			}
		}

		Vector3d Vector3d = getLook(0.0F);

		Vector3d Vector3d2 = RandomPositionGenerator.findAirTarget(FailgullEntity.this, 40, 3, Vector3d, ((float)Math.PI / 2F), 2, 1);
		final Vector3d groundTarget = RandomPositionGenerator.findGroundTarget(FailgullEntity.this, 40, 4, -2, Vector3d, (double) ((float) Math.PI / 2F));
		return Vector3d2 != null ? new BlockPos(Vector3d2) : groundTarget != null ? new BlockPos(groundTarget) : null;
	}

	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(TropicraftItems.FAILGULL_SPAWN_EGG.get());
	}

	class FollowLeaderGoal extends Goal {
		FollowLeaderGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
		}

		private boolean canFollow() {
			return !getIsFlockLeader() && hasFlockLeader();
		}

		@Override
		public boolean shouldExecute() {
			return canFollow() && getNavigator().noPath() && FailgullEntity.this.rand.nextInt(10) == 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return canFollow() && getNavigator().hasPath();
		}

		@Override
		public void startExecuting() {
			final Entity flockLeader = getFlockLeader();
			final PathNavigator navigator = getNavigator();
			if (flockLeader != null && flockLeader.getType() == TropicraftEntities.FAILGULL.get()) {
				navigator.setPath(navigator.getPathToPos(flockLeader.getPosition(), 1), 1.0D);
				return;
			}
			BlockPos Vector3d = getRandomLocation();
			if (Vector3d != null) {
				navigator.setPath(navigator.getPathToPos(Vector3d, 1), 1.0D);
			}

		}
	}

	class SetTravelDestination extends Goal {
		SetTravelDestination() {
			setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		private boolean shouldLead() {
			return getIsFlockLeader() || !hasFlockLeader();
		}

		@Override
		public boolean shouldExecute() {
			return shouldLead() && getNavigator().noPath() && getRNG().nextInt(10) == 0;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return shouldLead() && getNavigator().hasPath();
		}

		@Override
		public void startExecuting() {
			BlockPos Vector3d = getRandomLocation();
			if (Vector3d != null) {
				final PathNavigator navigator = getNavigator();
				navigator.setPath(navigator.getPathToPos(Vector3d, 1), 1.0D);
			}
		}
	}

	private static class ValidateFlockLeader extends Goal {
		final FailgullEntity mob;

		public ValidateFlockLeader(FailgullEntity failgullEntity) {
			mob = failgullEntity;
		}

		@Override
		public boolean shouldExecute() {
			if (mob.getIsFlockLeader()) {
				return false;
			}

			final Entity flockLeader = mob.getFlockLeader();
			return flockLeader == null || !flockLeader.isAlive();
		}

		@Override
		public void startExecuting() {
			mob.setFlockLeader(Optional.empty());
		}
	}

	private static class SelectFlockLeader extends Goal {
		final FailgullEntity mob;

		public SelectFlockLeader(FailgullEntity failgullEntity) {
			mob = failgullEntity;
		}

		@Override
		public boolean shouldExecute() {
			return !mob.hasFlockLeader();
		}

		@Override
		public void startExecuting() {
			List<FailgullEntity> list = mob.world.getEntitiesWithinAABB(FailgullEntity.class, mob.getBoundingBox().grow(10D, 10D, 10D));
			list.remove(mob);

			final Optional<FailgullEntity> oldest = list.stream().min(Comparator.comparingInt(FailgullEntity::getEntityId));
			// Found an older one nearby, set it as the flock leader
			if (oldest.isPresent() && !oldest.get().entityUniqueID.equals(mob.getUniqueID())) {
				final FailgullEntity oldestFailgull = oldest.get();
				oldestFailgull.setIsFlockLeader(true);
				oldestFailgull.setFlockLeader(Optional.empty());
				mob.setIsFlockLeader(false);
				mob.setFlockLeader(Optional.of(oldestFailgull.getUniqueID()));
			}
		}
	}
}
