package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class LastStandEnchantment extends Enchantment {
    protected LastStandEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return 25*pLevel;
    }

    @Override
    public int getMinCost(int pLevel) {
        return 25;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
