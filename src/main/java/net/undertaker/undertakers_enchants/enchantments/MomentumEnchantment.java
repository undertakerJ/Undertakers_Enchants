package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MomentumEnchantment extends Enchantment {
    protected MomentumEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
    return super.checkCompatibility(pOther)
        && pOther != ModEnchantments.SUDDEN_IMPACT.get()
        && pOther != ModEnchantments.CRITICAL_DAMAGE.get();
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
