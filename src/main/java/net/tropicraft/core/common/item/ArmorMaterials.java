package net.tropicraft.core.common.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.tropicraft.core.common.TropicraftTags;

public class ArmorMaterials {
    public static final IArmorMaterial ASHEN_MASK = new AshenMask();
    public static final IArmorMaterial NIGEL_STACHE = new NigelStache();
    public static final IArmorMaterial SCALE_ARMOR = createArmorMaterial(
            18,
            new int[] {2, 5, 6, 2},
            9,
            SoundEvents.ARMOR_EQUIP_CHAIN,
            Ingredient.of(TropicraftItems.SCALE.get()),
            "scale",
            0.5f,
            0.0F
    );
    public static final IArmorMaterial FIRE_ARMOR = createArmorMaterial(
            12,
            new int[] {2, 4, 5, 2},
            9,
            SoundEvents.ARMOR_EQUIP_IRON,
            null,
            "fire",
            0.1f,
            0.0F
    );
    public static final IArmorMaterial SCUBA = createArmorMaterial(
            10, 
            new int[] {0, 0, 0, 0},
            0,
            SoundEvents.ARMOR_EQUIP_GENERIC,
            null,
            "scuba_goggles",
            0,
            0.0F
    );

    private static class AshenMask implements IArmorMaterial {
        @Override
        public int getDurabilityForSlot(EquipmentSlotType slotIn) {
            return 10;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlotType slotIn) {
            return slotIn == EquipmentSlotType.HEAD ? 1 : 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(TropicraftTags.Items.ASHEN_MASKS);
        }

        @Override
        public String getName() {
            return "mask";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }

    private static class NigelStache implements IArmorMaterial {

        @Override
        public int getDurabilityForSlot(EquipmentSlotType slotIn) {
            return 10;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlotType slotIn) {
            return slotIn == EquipmentSlotType.HEAD ? 1 : 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(TropicraftItems.NIGEL_STACHE.get());
        }

        @Override
        public String getName() {
            return "nigel";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }

    public static IArmorMaterial createArmorMaterial(final int durability, final int[] dmgReduction, final int enchantability, final SoundEvent soundEvent,
                                                     final Ingredient repairMaterial, final String name, final float toughness, float knockbackResistance) {
        return new IArmorMaterial() {
            @Override
            public int getDurabilityForSlot(EquipmentSlotType equipmentSlotType) {
                return durability;
            }

            @Override
            public int getDefenseForSlot(EquipmentSlotType equipmentSlotType) {
                return dmgReduction[equipmentSlotType.getIndex()];
            }

            @Override
            public int getEnchantmentValue() {
                return enchantability;
            }

            @Override
            public SoundEvent getEquipSound() {
                return soundEvent;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return repairMaterial;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public float getToughness() {
                return toughness;
            }

            @Override
            public float getKnockbackResistance() {
                return knockbackResistance;
            }
        };
    }
}
