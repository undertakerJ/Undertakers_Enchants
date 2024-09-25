package net.undertaker.undertakers_enchants;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.level.BlockEvent;
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
          stepHeight.removeModifier(stepHeightId);
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
        if (hasEnchantment && player.getHealth() <= player.getMaxHealth() * 0.25) {
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 5, 1));
          player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 5, 1));
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 5, 1));
        } else if (hasEnchantment && player.getHealth() <= player.getMaxHealth() * 0.5) {
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 5, 1));
          player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 5, 0));
        } else if (hasEnchantment && player.getHealth() <= player.getMaxHealth() * 0.75) {
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
        int maxTargets = Math.min(entities.size(), enchantmentLevel);
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

  // PARADOXICAL UNBREAKING
  @SubscribeEvent
  public static void playerTickEvent9(TickEvent.PlayerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if (!event.player.level().isClientSide()) {
        ItemStack tool = event.player.getMainHandItem();
        Map<Enchantment, Integer> enchantments = tool.getAllEnchantments();
        int enchantmentLevel =
            enchantments.getOrDefault(ModEnchantments.PARADOXICAL_UNBREAKING.get(), 0);
        boolean hasEnchantment = enchantmentLevel > 0;
        if (hasEnchantment) {
          CompoundTag tag = tool.getOrCreateTag();
          if (!tag.contains("Unbreakable", Tag.TAG_BYTE)) {
            tag.putBoolean("Unbreakable", true);
            tool.setTag(tag);
          }
        }
      }
    }
  }

  @SubscribeEvent
  public static void onPlayerDeathEvent(LivingDeathEvent event) {
    if (!event.getEntity().level().isClientSide()) {
      if (event.getEntity() instanceof Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
          ItemStack itemStack = player.getInventory().getItem(i);
          Map<Enchantment, Integer> enchantments = itemStack.getAllEnchantments();
          int enchantmentLevel =
              enchantments.getOrDefault(ModEnchantments.PARADOXICAL_UNBREAKING.get(), 0);
          boolean hasEnchantment = enchantmentLevel > 0;
          if (hasEnchantment) {
            player.getInventory().removeItem(itemStack);
            player.sendSystemMessage(
                Component.translatable("message.paradoxical_item_player_death")
                    .withStyle(ChatFormatting.AQUA));
          }
        }
      }
    }
  }

  // NEGATION ZONE
  @SubscribeEvent
  public static void onLevelTick(TickEvent.LevelTickEvent event) {
    if (event.phase == TickEvent.Phase.END && !event.level.isClientSide()) {
      ServerLevel level = (ServerLevel) event.level;
      for (Player player : level.players()) {
        ItemStack legs = player.getInventory().getArmor(1);
        Map<Enchantment, Integer> enchantments = legs.getAllEnchantments();
        int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.NEGATION_ZONE.get(), 0);
        AABB zone = new AABB(player.blockPosition()).inflate(3);
        List<Projectile> projectiles = level.getEntitiesOfClass(Projectile.class, zone);
        if (enchantmentLevel > 0) {
          for (Projectile projectile : projectiles) {
            if (projectile.getOwner() != player && zone.contains(projectile.position())) {
              if (projectile instanceof SmallFireball) {
                projectile.discard();
              }
              projectile.setDeltaMovement(0, 0, 0);
              projectile.hasImpulse = true;
              projectile.setNoGravity(true);
              projectile.setTicksFrozen(20);
            } else {
              projectile.setTicksFrozen(0);
              projectile.setNoGravity(false);
              projectile.hasImpulse = true;
            }
          }
        } else {
          for (Projectile projectile : projectiles) {
            if (projectile.getTicksFrozen() > 0) {
              projectile.setTicksFrozen(0);
              projectile.setNoGravity(false);
              projectile.hasImpulse = true;
            }
          }
        }
      }
    }
  }

  // LOOT ROGUE
  private static final Map<UUID, Player> LAST_ATTACKERS = new HashMap<>();

  public static void setLastAttacker(LivingEntity entity, Player player) {
    clearLastAttacker(entity);
    LAST_ATTACKERS.put(entity.getUUID(), player);
  }

  public static Player getLastAttacker(LivingEntity entity) {
    return LAST_ATTACKERS.get(entity.getUUID());
  }

  public static void clearLastAttacker(LivingEntity entity) {
    LAST_ATTACKERS.remove(entity.getUUID());
  }

  @SubscribeEvent
  public static void onEntityAttack(LivingAttackEvent event) {
    if (event.getSource().getEntity() instanceof Player) {
      Player player = (Player) event.getSource().getEntity();
      setLastAttacker(event.getEntity(), player);
    }
  }

  @SubscribeEvent
  public static void onMobLootDrop(LivingDropsEvent event) {
    Level level = event.getEntity().level();
    Player player = getLastAttacker(event.getEntity());
    if (!level.isClientSide() && player != null) {
      ItemStack weapon = player.getMainHandItem();
      Map<Enchantment, Integer> enchantments = weapon.getAllEnchantments();
      int enchantmentLevel = enchantments.getOrDefault(ModEnchantments.LOOT_ROGUE.get(), 0);
      boolean hasEnchantment = enchantmentLevel > 0;
      if (hasEnchantment && RandomSource.create().nextFloat() <= 0.0834f * enchantmentLevel) {
        List<ItemEntity> originalDrops = (List<ItemEntity>) event.getDrops();
        List<ItemEntity> doubledDrops = new ArrayList<>();
        doubledDrops.addAll(originalDrops);
        for (ItemEntity drop : originalDrops) {
          ItemStack stack = drop.getItem();
          ItemStack doubledStack = stack.copy(); // Копируем предмет
          doubledDrops.add(
              new ItemEntity(level, drop.getX(), drop.getY(), drop.getZ(), doubledStack));
        }
        event.getDrops().clear();
        event.getDrops().addAll(doubledDrops);
      }
    }
  }

  // SVAROG
  private static final Map<UUID, BlockPos> playerBlockBreaks = new HashMap<>();

  @SubscribeEvent
  public static void onBlockBreak(BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();
    if (player instanceof ServerPlayer) {
      playerBlockBreaks.put(player.getUUID(), event.getPos());
    }
  }

  @SubscribeEvent
  public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
    if (event.getEntity() instanceof ItemEntity itemEntity
        && event.getLevel() instanceof ServerLevel level) {
      playerBlockBreaks.forEach(
          (playerUUID, blockPos) -> {
            if (itemEntity
                .position()
                .closerThan(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 2.0)) {
              ServerPlayer player = (ServerPlayer) level.getPlayerByUUID(playerUUID);
              if (player != null) {
                ItemStack mainHandItem = player.getMainHandItem();
                if (mainHandItem.isEnchanted()
                    && mainHandItem.getEnchantmentLevel(ModEnchantments.BREATH_OF_SVAROG.get())
                        > 0) {
                  ItemStack originalItem = itemEntity.getItem();
                  ItemStack smeltedItem = run(originalItem, level);
                  if (!ItemStack.matches(originalItem, smeltedItem)) {
                    itemEntity.setItem(smeltedItem);
                  }
                }
              }
            }
          });
    }
  }

  private static ItemStack run(ItemStack unSmelt, Level level) {
    if (!unSmelt.isEmpty() && !level.isClientSide()) {
      Optional<SmeltingRecipe> optional =
          level
              .getRecipeManager()
              .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(unSmelt), level);
      if (optional.isPresent()) {
        RegistryAccess registryAccess = level.registryAccess();
        ItemStack resultItem = optional.get().getResultItem(registryAccess);
        if (!resultItem.isEmpty()) {
          ItemStack smeltedItem = resultItem.copy();
          smeltedItem.setCount(unSmelt.getCount() * resultItem.getCount());

          if (RandomSource.create().nextFloat() < 0.05f) {
            smeltedItem.setCount(smeltedItem.getCount() * 2);
          }

          return smeltedItem;
        }
      }
    }
    return unSmelt;
  }

  // EXCAVATOR
  @SubscribeEvent
  public static void excavator(BlockEvent.BreakEvent event) {
    Level level = event.getPlayer().level();
    Player player = event.getPlayer();
    if (!level.isClientSide()) {
      ItemStack mainHandItem = player.getMainHandItem();
      if (mainHandItem.isEnchanted()
          && mainHandItem.getEnchantmentLevel(ModEnchantments.EXCAVATOR.get()) > 0
          && !player.isShiftKeyDown()
          && event.getState().getBlock() != Blocks.AIR) {
        breakBlocksInArea(event);
        mainHandItem.hurtAndBreak(8, player, player1 -> player.broadcastBreakEvent(player.getUsedItemHand()));
      }
    }
  }
  public static void breakBlocksInArea(BlockEvent.BreakEvent event) {
    Player player = event.getPlayer();
    Vec3 eyePosition = player.getEyePosition();
    Vec3 lookPosition = eyePosition.add(player.getLookAngle().scale(8));
    ClipContext clipContext =
            new ClipContext(
                    eyePosition, lookPosition, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
    BlockHitResult hitResult = player.level().clip(clipContext);
    Direction hitSide = hitResult.getDirection();
    BlockPos pos = event.getPos();
    int radiusX = 1;
    int radiusY = 1;
    int radiusZ = 1;
    for (int i = -radiusX; i <= radiusX; i++) {
      for (int j = -radiusY; j <= radiusY; j++) {
        for (int k = -radiusZ; k <= radiusZ; k++) {
          BlockPos targetPos = pos.offset(i, j, k);
          BlockState targetState = player.level().getBlockState(targetPos);
          Block targetBlock = targetState.getBlock();
          if (targetBlock == Blocks.BEDROCK ||
                  targetBlock == Blocks.OBSIDIAN ||
                  targetBlock == Blocks.REINFORCED_DEEPSLATE ||
                  targetBlock == Blocks.END_GATEWAY ||
                  targetBlock == Blocks.END_PORTAL ||
                  targetBlock == Blocks.END_PORTAL_FRAME ||
                  targetBlock == Blocks.BEACON ||
                  targetBlock == Blocks.NETHER_PORTAL ||
                  targetBlock == Blocks.ENCHANTING_TABLE) {
            return;
          }
        }
      }
    }
    switch (hitSide.getAxis()) {
      case X:
        breakBlocksVertically(event.getPlayer(), pos, 0, radiusY, radiusZ);
        break;
      case Y:
        breakBlocksHorizontally(event.getPlayer(), pos, radiusX, radiusZ);
        break;
      case Z:
        breakBlocksVertically(event.getPlayer(), pos, radiusX, radiusY, 0);
        break;
    }
  }
  private static void breakBlocksHorizontally(Player player, BlockPos pos, int radiusX, int radiusZ) {
    for (int i = -radiusX; i <= radiusX; i++) {
      for (int j = -radiusZ; j <= radiusZ; j++) {
        BlockPos targetPos = pos.offset(i, 0, j);
        player.level().destroyBlock(targetPos, true);
      }
    }
  }
  private static void breakBlocksVertically(Player player, BlockPos pos, int radiusX, int radiusY, int radiusZ) {
    for (int i = -radiusX; i <= radiusX; i++) {
      for (int j = -radiusY; j <= radiusY; j++) {
        for (int k = -radiusZ; k <= radiusZ; k++) {
          BlockPos targetPos = pos.offset(i, j, k);
          player.level().destroyBlock(targetPos, true);
        }
      }
    }
  }
  //LAST_STAND
  @SubscribeEvent
  public static void lastStand(TickEvent.PlayerTickEvent event){
    if (event.phase == TickEvent.Phase.END) {
      Player player = event.player;
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
                    itemStack -> itemStack.getEnchantmentLevel(ModEnchantments.LAST_STAND.get()))
                .sum();

        boolean hasEnchantment = totalEnchantmentLevel > 0;
        AttributeInstance armor = player.getAttributes().getInstance(Attributes.ARMOR);
        UUID armorId = UUID.fromString("9eae3ca0-8684-4bd8-8f48-90813e0ce138");
        double armorModifier = totalEnchantmentLevel * 0.03;
        AttributeModifier oldModifier = armor.getModifier(armorId);
        AttributeModifier newModifier =
            new AttributeModifier(
                armorId,
                "undertakers_enchants.armor",
                armorModifier,
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (hasEnchantment && oldModifier == null) {
          armor.addTransientModifier(newModifier);
        } else if (!hasEnchantment && oldModifier != null) {
          armor.removeModifier(armorId);
        } else if (hasEnchantment && newModifier.getAmount() != oldModifier.getAmount()) {
          armor.removeModifier(armorId);
          armor.addTransientModifier(newModifier);
        }
      }
    }
  }

}
