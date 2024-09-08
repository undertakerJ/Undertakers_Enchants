package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class ParadoxicalUnderbreakingEnchantment extends Enchantment {
    protected ParadoxicalUnderbreakingEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }
    public int getMinCost(int pEnchantmentLevel) {
        return 30;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return 50;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther)
                && pOther != Enchantments.VANISHING_CURSE
                && pOther != Enchantments.BLOCK_FORTUNE
                && pOther != Enchantments.SILK_TOUCH;
    }

    @Override
    public boolean isCurse() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
