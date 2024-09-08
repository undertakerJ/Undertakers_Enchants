package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class TravellerEnchantment extends Enchantment {
    public TravellerEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);

    }

    public int getMinCost(int pLevel) {
        return pLevel * 20;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
    return super.checkCompatibility(pOther) && pOther != Enchantments.SOUL_SPEED;
    }

    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 40;
    }
    public int getMaxLevel() {
        return 3;
    }
}
