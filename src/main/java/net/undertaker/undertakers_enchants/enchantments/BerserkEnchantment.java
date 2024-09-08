package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BerserkEnchantment extends Enchantment {
    protected BerserkEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    public int getMinCost(int pLevel) {
        return pLevel * 30;
    }


    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 30;
    }
}

