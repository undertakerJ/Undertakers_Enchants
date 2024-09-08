package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class GoodHealthEnchantment extends Enchantment {
    protected GoodHealthEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != ModEnchantments.CURSE_OF_BAD_HEALTH.get();
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
    public int getMinCost(int pLevel) {
        return pLevel * 30;
    }


    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 30;
    }
}
