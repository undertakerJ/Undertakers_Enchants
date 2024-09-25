package net.undertaker.undertakers_enchants.enchantments;


import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class NegationZoneEnchantment extends Enchantment {
    protected NegationZoneEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    public int getMinCost(int pLevel) {
        return pLevel * 25;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
    return super.checkCompatibility(pOther)
        && pOther != Enchantments.PROJECTILE_PROTECTION
        && pOther != Enchantments.BLAST_PROTECTION
        && pOther != Enchantments.FALL_PROTECTION
        && pOther != Enchantments.FIRE_PROTECTION
        && pOther != Enchantments.ALL_DAMAGE_PROTECTION
        && pOther != Enchantments.THORNS
        && pOther != Enchantments.UNBREAKING
        && pOther != Enchantments.MENDING
        && pOther != Enchantments.SWIFT_SNEAK
        && pOther != ModEnchantments.GOOD_HEALTH.get()  ;
    }

    public int getMaxCost(int pLevel) {
        return this.getMinCost(pLevel) + 30;
    }


    @Override
    public int getMaxLevel() {
        return 1;
    }
}
