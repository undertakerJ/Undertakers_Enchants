package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CurseOfBadHealthEnchantment extends Enchantment {
    protected CurseOfBadHealthEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    public int getMinCost(int pEnchantmentLevel) {
        return 25;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return 50;
    }


    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
    return super.checkCompatibility(pOther) && pOther != ModEnchantments.GOOD_HEALTH.get();
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
