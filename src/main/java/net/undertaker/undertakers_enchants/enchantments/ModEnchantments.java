package net.undertaker.undertakers_enchants.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.undertaker.undertakers_enchants.UndertakersEnchants;

public class ModEnchantments {

  private static final EquipmentSlot[] ARMOR_SLOTS;

  static {
    ARMOR_SLOTS =
        new EquipmentSlot[] {
          EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };
  }

  public static final DeferredRegister<Enchantment> ENCHANTMENTS =
      DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, UndertakersEnchants.MOD_ID);

  public static RegistryObject<Enchantment> ARMOR_SHREDDER =
      ENCHANTMENTS.register(
          "armor_shredder",
          () ->
              new ArmorShredderEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> TRAVELLER =
      ENCHANTMENTS.register(
          "traveller",
          () ->
              new TravellerEnchantment(
                  Enchantment.Rarity.RARE,
                  EnchantmentCategory.ARMOR_FEET,
                  EquipmentSlot.FEET));
  public static RegistryObject<Enchantment> MAGNETIC =
      ENCHANTMENTS.register(
          "magnetic",
          () ->
              new MagneticEnchantment(
                  Enchantment.Rarity.UNCOMMON, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> CURSE_OF_BAD_HEALTH =
      ENCHANTMENTS.register(
          "curse_of_bad_health",
          () ->
              new CurseOfBadHealthEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR, ARMOR_SLOTS));
  public static RegistryObject<Enchantment> GOOD_HEALTH =
      ENCHANTMENTS.register(
          "good_health",
          () ->
              new GoodHealthEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR, ARMOR_SLOTS));
  public static RegistryObject<Enchantment> CRITICAL_DAMAGE =
      ENCHANTMENTS.register(
          "critical_damage",
          () ->
              new CriticalDamageEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> SUDDEN_IMPACT =
      ENCHANTMENTS.register(
          "sudden_impact",
          () ->
              new SuddenImpactEnchantment(
                  Enchantment.Rarity.RARE,
                  EnchantmentCategory.WEAPON,
                  EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> LIFE_STEAL =
      ENCHANTMENTS.register(
          "life_steal",
          () ->
              new LifeStealEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> NIGHT_VISION_ENCHANT =
      ENCHANTMENTS.register(
          "night_vision_enchant",
          () ->
              new NightVisionEnchantment(
                  Enchantment.Rarity.UNCOMMON,
                  EnchantmentCategory.ARMOR_HEAD,
                  EquipmentSlot.HEAD));
  public static RegistryObject<Enchantment> BERSERK =
      ENCHANTMENTS.register(
          "berserk",
          () ->
              new BerserkEnchantment(
                  Enchantment.Rarity.RARE,
                  EnchantmentCategory.ARMOR_CHEST,
                  EquipmentSlot.CHEST));
  public static RegistryObject<Enchantment> CURSE_OF_GRAVITY =
      ENCHANTMENTS.register(
          "curse_of_gravity",
          () ->
              new CurseOfGravityEnchantment(
                  Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_LEGS, EquipmentSlot.LEGS));
  public static RegistryObject<Enchantment> PRESS_THE_ATTACK =
      ENCHANTMENTS.register(
          "press_the_attack",
          () ->
              new PressTheAttackEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.BOW, EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> BOUNCY_ARROWS =
      ENCHANTMENTS.register(
          "bouncy_arrows",
          () ->
              new BouncyArrowsEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.BOW, EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> LONG_HANDED =
      ENCHANTMENTS.register(
          "long_handed",
          () ->
              new LongHandedEnchantment(
                  Enchantment.Rarity.RARE, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND));
  public static RegistryObject<Enchantment> PARADOXICAL_UNBREAKING =
      ENCHANTMENTS.register(
          "paradoxical_unbreaking",
          () ->
              new ParadoxicalUnderbreakingEnchantment(
                  Enchantment.Rarity.VERY_RARE, EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND));

  public static void register(IEventBus eventBus) {
    ENCHANTMENTS.register(eventBus);
  }
}
