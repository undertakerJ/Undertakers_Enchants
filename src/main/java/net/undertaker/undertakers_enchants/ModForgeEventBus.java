package net.undertaker.undertakers_enchants;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.undertaker.undertakers_enchants.enchantments.ModEnchantments;

import java.util.*;

import static net.minecraftforge.common.ForgeMod.STEP_HEIGHT_ADDITION;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = UndertakersEnchants.MOD_ID)
public class ModForgeEventBus {
  // TRAVELLER ENCHANTMENT
  @SubscribeEvent
  public static void playerTickEvent1(TickEvent.PlayerTickEvent playerTickEvent) {
    if (playerTickEvent.phase == TickEvent.Phase.END) {

      Player player = playerTickEvent.player;
      ItemStack boots = player.getInventory().getArmor(0);
      Map<Enchantment, Integer> enchantments = boots.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.TRAVELLER.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      AttributeInstance speed = player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
      AttributeInstance stepHeight = player.getAttributes().getInstance(STEP_HEIGHT_ADDITION.get());
      UUID speedId = UUID.fromString("d3b1ec47-8027-4205-8ecd-144ffd7220df");
      UUID stepHeightId = UUID.fromString("2eea199a-e9ca-4bf2-824c-b5a52e99a37c");
      double modifierValue = enchantmentLevel * 0.15;
      AttributeModifier oldModifier = speed.getModifier(speedId);
      AttributeModifier newModifier =
          new AttributeModifier(
              speedId,
              "undertakers_enchants.traveller_speed",
              modifierValue,
              AttributeModifier.Operation.MULTIPLY_BASE);
      if (enchantmentLevel >= 3) {
        if (stepHeight.getModifier(stepHeightId) == null) {
          stepHeight.addTransientModifier(
              new AttributeModifier(
                  stepHeightId,
                  "undertakers_enchants.step_height",
                  1.0,
                  AttributeModifier.Operation.ADDITION));
        }
      } else {
        AttributeModifier existingModifier = stepHeight.getModifier(stepHeightId);
        if (existingModifier != null) {
          stepHeight.removeModifier(existingModifier);
        }
      }

      if (hasEnchantment && oldModifier == null) {
        speed.addTransientModifier(newModifier);
      } else if (!hasEnchantment && oldModifier != null) {
        speed.removeModifier(speedId);
      } else if (hasEnchantment && newModifier.getAmount() != oldModifier.getAmount()) {
        speed.removeModifier(speedId);
        speed.addTransientModifier(newModifier);
      }
    }
  }

  // MAGNETIC ENCHANTMENT
  @SubscribeEvent
  public static void playerTickEvent2(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      Player player = event.player;
      ItemStack heldItem = player.getMainHandItem();
      Map<Enchantment, Integer> enchantments = heldItem.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.MAGNETIC.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      if (hasEnchantment) {
        double radius = enchantmentLevel * 3.0;
        double speed = enchantmentLevel * 0.25;

        List<ItemEntity> entities =
            player
                .level()
                .getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(radius));

        for (Entity entity : entities) {
          if (entity instanceof ItemEntity itemEntity) {
            Vec3 playerPos = player.position();
            Vec3 itemPos = itemEntity.position();
            Vec3 direction = playerPos.subtract(itemPos).normalize();

            itemEntity.setDeltaMovement(direction.scale(speed));
          }
        }
      }
    }
  }

  // CURSE OF BAD HEALTH
  @SubscribeEvent
  public static void playerTickEvent3(TickEvent.PlayerTickEvent playerTickEvent) {
    if (playerTickEvent.phase == TickEvent.Phase.END) {

      Player player = playerTickEvent.player;
      Level level = player.level();
      if (!level.isClientSide()) {
        ItemStack[] armorSet =
            new ItemStack[] {
              player.getInventory().getArmor(0),
              player.getInventory().getArmor(1),
              player.getInventory().getArmor(2),
              player.getInventory().getArmor(3)
            };
        int totalEnchantmentLevel =
            Arrays.stream(armorSet)
                .filter(itemStack -> !itemStack.isEmpty())
                .mapToInt(
                    itemStack ->
                        itemStack.getEnchantmentLevel(ModEnchantments.CURSE_OF_BAD_HEALTH.get()))
                .sum();
        boolean hasEnchantment = totalEnchantmentLevel > 0;
        AttributeInstance health = player.getAttributes().getInstance(Attributes.MAX_HEALTH);
        UUID healthId = UUID.fromString("d4f9c217-bdad-4503-af79-b7bef5b8eea4");
        double healthModifier = totalEnchantmentLevel * -0.1;
        AttributeModifier oldModifier = health.getModifier(healthId);
        AttributeModifier newModifier =
            new AttributeModifier(
                healthId,
                "undertakers_enchants.curse_of_bad_health",
                healthModifier,
                AttributeModifier.Operation.MULTIPLY_BASE);

        if (hasEnchantment && oldModifier == null) {
          health.addTransientModifier(newModifier);
        } else if (!hasEnchantment && oldModifier != null) {
          health.removeModifier(healthId);
        } else if (hasEnchantment && newModifier.getAmount() != oldModifier.getAmount()) {
          health.removeModifier(healthId);
          health.addTransientModifier(newModifier);
        }
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        if (currentHealth > maxHealth) {
          player.setHealth(maxHealth);
        }
      }
    }
  }

  // GOOD HEALTH
  @SubscribeEvent
  public static void playerTickEvent4(TickEvent.PlayerTickEvent playerTickEvent) {
    if (playerTickEvent.phase == TickEvent.Phase.END) {

      Player player = playerTickEvent.player;
      Level level = player.level();
      if (!level.isClientSide()) {
        ItemStack[] armorSet =
            new ItemStack[] {
              player.getInventory().getArmor(0),
              player.getInventory().getArmor(1),
              player.getInventory().getArmor(2),
              player.getInventory().getArmor(3)
            };

        int totalEnchantmentLevel =
            Arrays.stream(armorSet)
                .filter(itemStack -> !itemStack.isEmpty())
                .mapToInt(
                    itemStack -> itemStack.getEnchantmentLevel(ModEnchantments.GOOD_HEALTH.get()))
                .sum();

        boolean hasEnchantment = totalEnchantmentLevel > 0;
        AttributeInstance health = player.getAttributes().getInstance(Attributes.MAX_HEALTH);
        UUID healthId = UUID.fromString("314bfd0a-cb9f-433f-8fc1-f6c350a92e0c");
        double healthModifier = totalEnchantmentLevel * 0.05;
        AttributeModifier oldModifier = health.getModifier(healthId);
        AttributeModifier newModifier =
            new AttributeModifier(
                healthId,
                "undertakers_enchants.good_heath",
                healthModifier,
                AttributeModifier.Operation.MULTIPLY_BASE);

        if (hasEnchantment && oldModifier == null) {
          health.addTransientModifier(newModifier);
        } else if (!hasEnchantment && oldModifier != null) {
          health.removeModifier(healthId);
        } else if (hasEnchantment && newModifier.getAmount() != oldModifier.getAmount()) {
          health.removeModifier(healthId);
          health.addTransientModifier(newModifier);
        }
      }
    }
  }

  // CRITICAL DAMAGE
  @SubscribeEvent
  public static void onCriticalHit(CriticalHitEvent event) {
    Player player = event.getEntity();
    Level level = player.level();
    ItemStack weapon = player.getMainHandItem();
    Map<Enchantment, Integer> enchantments = weapon.getAllEnchantments();
    int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.CRITICAL_DAMAGE.get(), 0);
    boolean hasEnchantment = enchantmentLevel > 0;
    if (!level.isClientSide() && hasEnchantment && enchantmentLevel >= 2) {
      event.setDamageModifier(
          (float) (event.getDamageModifier() * (0.66 + 0.2 * enchantmentLevel)));
    }
  }

  // SUDDEN IMPACT
  @SubscribeEvent
  public static void onLivingDamage1(LivingDamageEvent event) {
    Entity source = event.getSource().getDirectEntity();
    if (source instanceof Player) {
      Player player = (Player) source;
      LivingEntity livingEntity = event.getEntity();
      Level level = livingEntity.level();
      ItemStack weapon = player.getMainHandItem();
      Map<Enchantment, Integer> enchantments = weapon.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.SUDDEN_IMPACT.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      if (!level.isClientSide()
          && hasEnchantment
          && livingEntity.getHealth() == livingEntity.getMaxHealth()) {
        float currentDamage = event.getAmount();
        float damageScale = (float) (1 + 0.2 * enchantmentLevel);
        event.setAmount(currentDamage * damageScale);
      }
    }
  }

  // LIFE STEAL
  @SubscribeEvent
  public static void onLivingDamage2(LivingDamageEvent event) {
    Entity source = event.getSource().getDirectEntity();
    if (source instanceof Player) {
      Player player = (Player) source;
      LivingEntity livingEntity = event.getEntity();
      Level level = livingEntity.level();
      ItemStack weapon = player.getMainHandItem();
      Map<Enchantment, Integer> enchantments = weapon.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.LIFE_STEAL.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      if (!level.isClientSide() && hasEnchantment) {
        float currentDamage = event.getAmount();
        float healScale = (float) (0.05 * enchantmentLevel);
        player.heal(currentDamage * healScale);
      }
    }
  }

  // NIGHT VISION
  @SubscribeEvent
  public static void playerTickEvent5(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      Player player = event.player;
      ItemStack helm = player.getInventory().getArmor(3);
      if (helm.isEmpty()) {
        return;
      }
      Map<Enchantment, Integer> enchantments = helm.getAllEnchantments();
      int enchantmentLevel =
          enchantments.getOrDefault(ModEnchantments.NIGHT_VISION_ENCHANT.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      if (hasEnchantment) {
        player.addEffect(
            new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 11, 0, false, false, false));
      }
    }
  }

  // BERSERK
  @SubscribeEvent
  public static void playerTickEvent6(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      Player player = event.player;
      ItemStack chest = player.getInventory().getArmor(2);
      Map<Enchantment, Integer> enchantments = chest.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.BERSERK.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      if (!player.level().isClientSide() && player.tickCount % 80 == 0) {
        if (hasEnchantment && player.getHealth() <= player.getMaxHealth()*0.25) {
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 5, 1));
          player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 5, 1));
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 5, 1));
        } else if (hasEnchantment && player.getHealth() <= player.getMaxHealth()*0.5) {
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 5, 1));
          player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 5, 0));
        } else if (hasEnchantment && player.getHealth() <= player.getMaxHealth()*0.75) {
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 5, 0));
        }
      }
    }
  }

  // CURSE OF GRAVITY
  @SubscribeEvent
  public static void playerTickEvent7(TickEvent.PlayerTickEvent playerTickEvent) {
    if (playerTickEvent.phase == TickEvent.Phase.END) {

      Player player = playerTickEvent.player;
      Level level = player.level();
      if (!level.isClientSide()) {
        ItemStack legs = player.getInventory().getArmor(1);
        Map<Enchantment, Integer> enchantments = legs.getAllEnchantments();
        int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.CURSE_OF_GRAVITY.get(), 0);
        AttributeInstance gravity =
            player.getAttributes().getInstance(ForgeMod.ENTITY_GRAVITY.get());
        UUID gravityId = UUID.fromString("f61f3d37-947c-4ae2-af73-fec955325331");
        boolean hasEnchantment = enchantmentLevel > 0;
        AttributeModifier oldModifier = gravity.getModifier(gravityId);
        AttributeModifier newModifier =
            new AttributeModifier(
                gravityId,
                "undertakers_enchants.curse_of_gravity",
                4,
                AttributeModifier.Operation.MULTIPLY_BASE);

        if (hasEnchantment && oldModifier == null) {
          gravity.addTransientModifier(newModifier);
        } else if (!hasEnchantment && oldModifier != null) {
          gravity.removeModifier(gravityId);
        } else if (hasEnchantment && newModifier.getAmount() != oldModifier.getAmount()) {
          gravity.removeModifier(gravityId);
          gravity.addTransientModifier(newModifier);
        }
      }
    }
  }

  // PRESS THE ATTACK
  @SubscribeEvent
  public static void onLivingDamage3(LivingDamageEvent event) {
    Entity source = event.getSource().getDirectEntity();
    DamageSource damageSource = event.getSource();
    if (source instanceof Arrow arrow && arrow.getOwner() instanceof Player) {
      Player player = (Player) arrow.getOwner();
      LivingEntity livingEntity = event.getEntity();
      Level level = livingEntity.level();
      ItemStack bow = player.getMainHandItem();
      Map<Enchantment, Integer> enchantments = bow.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.PRESS_THE_ATTACK.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      if (!level.isClientSide() && hasEnchantment) {
        CompoundTag tag = livingEntity.getPersistentData();
        String key = "pressTheAttackHits";
        int hits = tag.getInt(key);
        hits++;
        tag.putInt(key, hits);

        float baseDamage = event.getAmount();
        float growthRate = 0.1f;
        float additionalDamage = 1 + ((growthRate * enchantmentLevel) * hits);

        float newDamage = baseDamage * additionalDamage;

        event.setAmount(newDamage);
        livingEntity.addAdditionalSaveData(tag);
      }
    }
  }

  // BOUNCY ARROWS
  @SubscribeEvent
  public static void onLivingDamage4(LivingDamageEvent event) {
    Entity source = event.getSource().getDirectEntity();
    if (source instanceof Arrow arrow && arrow.getOwner() instanceof Player) {
      Player player = (Player) arrow.getOwner();
      LivingEntity livingEntity = event.getEntity();
      Level level = livingEntity.level();
      ItemStack bow = player.getMainHandItem();
      Map<Enchantment, Integer> enchantments = bow.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.BOUNCY_ARROWS.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      Holder<DamageType> arrowHolder =
          level
              .registryAccess()
              .registryOrThrow(Registries.DAMAGE_TYPE)
              .getHolderOrThrow(DamageTypes.ARROW);
      if (!level.isClientSide() && hasEnchantment) {
        Vec3 start = arrow.position();
        Vec3 direction = arrow.getDeltaMovement().normalize();
        Vec3 end = start.add(direction.scale(5));
        AABB box = new AABB(start, end).inflate(1.0);
        List<Entity> entities =
            level.getEntities(player, box, e -> e instanceof LivingEntity && e != livingEntity);
        entities.sort(Comparator.comparingDouble(e -> e.distanceToSqr(start)));
        int maxTargets =
            Math.min(entities.size(), enchantmentLevel);
        for (int i = 0; i < maxTargets; i++) {
          LivingEntity nextTarget = (LivingEntity) entities.get(i);
          if (nextTarget != null) {
            DamageSource damageSource = new DamageSource(arrowHolder, player);
            nextTarget.hurt(damageSource, event.getAmount() * 0.75F);
          }
        }
      }
    }
  }

  // LONG HANDED
  @SubscribeEvent
  public static void playerTickEvent8(TickEvent.PlayerTickEvent playerTickEvent) {
    if (playerTickEvent.phase == TickEvent.Phase.END) {

      Player player = playerTickEvent.player;
      Level level = player.level();
      if (!level.isClientSide()) {
        ItemStack tool = player.getMainHandItem();
        Map<Enchantment, Integer> enchantments = tool.getAllEnchantments();
        int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.LONG_HANDED.get(), 0);
        AttributeInstance blockReach =
                player.getAttributes().getInstance(ForgeMod.BLOCK_REACH.get());
        UUID blockReachId = UUID.fromString("24f067fb-9a7f-41ee-85a1-350acee46540");
        boolean hasEnchantment = enchantmentLevel > 0;
        AttributeModifier oldModifier = blockReach.getModifier(blockReachId);
        AttributeModifier newModifier =
                new AttributeModifier(
                        blockReachId,
                        "undertakers_enchants.block_reach",
                        enchantmentLevel,
                        AttributeModifier.Operation.ADDITION);

        if (hasEnchantment && oldModifier == null) {
          blockReach.addTransientModifier(newModifier);
        } else if (!hasEnchantment && oldModifier != null) {
          blockReach.removeModifier(blockReachId);
        } else if (hasEnchantment && newModifier.getAmount() != oldModifier.getAmount()) {
          blockReach.removeModifier(blockReachId);
          blockReach.addTransientModifier(newModifier);
        }
      }
    }
  }
}
