package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class NightVisionEnchantment extends Enchantment {
    protected NightVisionEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
    public int getMinCost(int pLevel) {
        return pLevel * 30;
    }


    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 30;
    }
}

