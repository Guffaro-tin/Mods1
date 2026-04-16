package com.tuonome.devilfruitmod;

import com.tuonome.devilfruitmod.network.PacketHandler;
import com.tuonome.devilfruitmod.network.UseAbilityPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DevilFruitMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.LOGIA_TOGGLE);
        event.register(KeyBindings.ABILITY_1);
        event.register(KeyBindings.ABILITY_2);
        event.register(KeyBindings.ABILITY_3);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        while (KeyBindings.LOGIA_TOGGLE.consumeClick())
            PacketHandler.sendToServer(new UseAbilityPacket(0));
        while (KeyBindings.ABILITY_1.consumeClick())
            PacketHandler.sendToServer(new UseAbilityPacket(1));
        while (KeyBindings.ABILITY_2.consumeClick())
            PacketHandler.sendToServer(new UseAbilityPacket(2));
        while (KeyBindings.ABILITY_3.consumeClick())
            PacketHandler.sendToServer(new UseAbilityPacket(3));
    }
}
