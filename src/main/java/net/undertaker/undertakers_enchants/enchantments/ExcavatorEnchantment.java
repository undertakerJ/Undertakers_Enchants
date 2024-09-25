package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;

import static net.minecraft.core.Holder.direct;

public class ExcavatorEnchantment extends Enchantment {
    protected ExcavatorEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != Enchantments.BLOCK_EFFICIENCY;
    }

    @Override
    public int getMinCost(int pLevel) {
        return 30;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return 50;
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
    if (pStack.getItem() instanceof PickaxeItem || pStack.getItem() instanceof ShovelItem) {
      return true;
        }
        return false;
    }
}
