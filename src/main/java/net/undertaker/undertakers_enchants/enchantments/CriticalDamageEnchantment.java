package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CriticalDamageEnchantment extends Enchantment {
    protected CriticalDamageEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }
    public int getMinCost(int pLevel) {
        return pLevel * 20;
    }


    @Override
    public int getMaxLevel() {
        return 5;
    }
    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != ModEnchantments.SUDDEN_IMPACT.get();
    }

    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 30;
    }
}
