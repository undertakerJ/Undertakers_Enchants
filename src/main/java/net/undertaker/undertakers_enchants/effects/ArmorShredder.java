package net.undertaker.undertakers_enchants.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ArmorShredder extends MobEffect {

  protected ArmorShredder() {
    super(MobEffectCategory.HARMFUL, 0x0033FFFF);
    addAttributeModifier(
        Attributes.ARMOR,
        "ba13527f-58e0-4b97-bbc0-ea858f44dc82",
        -0.06D,
        AttributeModifier.Operation.MULTIPLY_TOTAL);
  }
}
