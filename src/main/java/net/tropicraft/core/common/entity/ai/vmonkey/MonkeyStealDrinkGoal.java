package net.tropicraft.core.common.entity.ai.vmonkey;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.tropicraft.core.common.drinks.Drink;
import net.tropicraft.core.common.drinks.MixerRecipes;
import net.tropicraft.core.common.entity.neutral.VMonkeyEntity;
import net.tropicraft.core.common.item.CocktailItem;

import java.util.EnumSet;

public class MonkeyStealDrinkGoal extends Goal {
    private VMonkeyEntity entity;

    public MonkeyStealDrinkGoal(VMonkeyEntity monkey) {
        this.entity = monkey;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getOwner() == null && VMonkeyEntity.FOLLOW_PREDICATE.test(entity.getFollowing()) && !entity.selfHoldingDrink(Drink.PINA_COLADA);
    }

    @Override
    public boolean canUse() {
        return entity.getOwner() == null && VMonkeyEntity.FOLLOW_PREDICATE.test(entity.getFollowing()) && !entity.selfHoldingDrink(Drink.PINA_COLADA) && entity.isAggressive();
    }

    private void leapTowardTarget() {
        LivingEntity leapTarget = entity.getTarget();

        if (leapTarget == null) return;

        double d0 = leapTarget.getX() - entity.getX();
        double d1 = leapTarget.getZ() - entity.getZ();
        float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
        final Vector3d motion = entity.getDeltaMovement();

        if ((double)f >= 1.0E-4D) {
            entity.setDeltaMovement(motion.add(d0 / (double)f * 0.5D * 0.800000011920929D + motion.x * 0.20000000298023224D, 0, d1 / (double)f * 0.5D * 0.800000011920929D + motion.z * 0.20000000298023224D));
        }

        entity.setDeltaMovement(new Vector3d(motion.x, 0.25, motion.z));
    }

    @Override
    public void tick() {
        if (entity.distanceToSqr(entity.getFollowing()) < 4.0F) {
            for (final Hand hand : Hand.values()) {
                if (CocktailItem.getDrink(entity.getFollowing().getItemInHand(hand)) == Drink.PINA_COLADA) {
                    leapTowardTarget();
                    entity.getFollowing().setItemInHand(hand, ItemStack.EMPTY);
                    entity.setItemInHand(hand, MixerRecipes.getItemStack(Drink.PINA_COLADA));
                }
            }
        }
    }
}
