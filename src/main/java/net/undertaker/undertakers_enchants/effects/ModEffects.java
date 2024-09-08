package net.undertaker.undertakers_enchants.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.undertaker.undertakers_enchants.UndertakersEnchants;

public class ModEffects extends MobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, UndertakersEnchants.MOD_ID);

    public static final RegistryObject<MobEffect> ARMOR_SHRED_EFFECT =
            MOB_EFFECTS.register("armor_shred_effect", ArmorShredder::new);


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}