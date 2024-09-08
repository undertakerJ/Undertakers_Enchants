package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.undertaker.undertakers_enchants.effects.ModEffects;

public class ArmorShredderEnchantment extends Enchantment {
  public ArmorShredderEnchantment(
      Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
    super(pRarity, EnchantmentCategory.WEAPON, pApplicableSlots);
  }

  @Override
  public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
    if (!pAttacker.level().isClientSide()) {
      LivingEntity attacked = (LivingEntity) pTarget;
      if (pLevel == 5) {
        attacked.addEffect(
            new MobEffectInstance(ModEffects.ARMOR_SHRED_EFFECT.get(), 5 * 20, 4, false, false));
      }
      if (pLevel == 4) {
        attacked.addEffect(
            new MobEffectInstance(ModEffects.ARMOR_SHRED_EFFECT.get(), 5 * 20, 3, false, false));
      }
      if (pLevel == 3) {
        attacked.addEffect(
            new MobEffectInstance(ModEffects.ARMOR_SHRED_EFFECT.get(), 5 * 20, 2, false, false));
      }
      if (pLevel == 2) {
        attacked.addEffect(
            new MobEffectInstance(ModEffects.ARMOR_SHRED_EFFECT.get(), 5 * 20, 1, false, false));
      }
      if (pLevel == 1) {
        attacked.addEffect(
            new MobEffectInstance(ModEffects.ARMOR_SHRED_EFFECT.get(), 5 * 20, 0, false, false));
      }
    }

    super.doPostAttack(pAttacker, pTarget, pLevel);
  }
  public int getMinCost(int pEnchantmentLevel) {
    return 25;
  }

  public int getMaxCost(int pEnchantmentLevel) {
    return 50;
  }

  @Override
  public int getMaxLevel() {
    return 5;
  }
}
