package com.tuonome.devilfruitmod;

import com.tuonome.devilfruitmod.init.ItemInit;
import com.tuonome.devilfruitmod.network.PacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DevilFruitMod.MOD_ID)
public class DevilFruitMod {
    public static final String MOD_ID = "devilfruitmod";
    private static final Logger LOGGER = LogManager.getLogger();

    public DevilFruitMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemInit.ITEMS.register(modEventBus);
        PacketHandler.register();
        LOGGER.info("Devil Fruit Mod caricata!");
    }
}
