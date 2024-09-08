package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MagneticEnchantment extends Enchantment {
    protected MagneticEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }
    public int getMinCost(int pLevel) {
        return pLevel * 20;
    }


    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 30;
    }
    public int getMaxLevel() {
        return 3;
    }
}
