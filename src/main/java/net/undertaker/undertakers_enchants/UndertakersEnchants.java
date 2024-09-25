package net.undertaker.undertakers_enchants;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.undertaker.undertakers_enchants.effects.ModEffects;
import net.undertaker.undertakers_enchants.enchantments.ModEnchantments;
import org.slf4j.Logger;

@Mod(UndertakersEnchants.MOD_ID)
public class UndertakersEnchants
{
    public static final String MOD_ID = "undertakers_enchants";
    public UndertakersEnchants()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEnchantments.register(modEventBus);
        ModEffects.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

}
