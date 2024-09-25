package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class TheBreatheOfSvarogEnchantment extends Enchantment {
  protected TheBreatheOfSvarogEnchantment(
      Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
    super(pRarity, pCategory, pApplicableSlots);
  }

  @Override
  public boolean isTreasureOnly() {
    return true;
  }

  public int getMinCost(int pLevel) {
    return pLevel * 25;
  }

  public int getMaxCost(int pLevel) {
    return this.getMinCost(pLevel) + 30;
  }

  @Override
  protected boolean checkCompatibility(Enchantment pOther) {
    return super.checkCompatibility(pOther)
        && pOther != Enchantments.BLOCK_FORTUNE
        && pOther != Enchantments.SILK_TOUCH;
  }

  @Override
  public int getMaxLevel() {
    return 1;
  }
}
