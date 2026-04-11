package com.tuonome.devilfruitmod.network;

import com.tuonome.devilfruitmod.DevilFruitMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String VERSION = "1";
    private static int id = 0;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(DevilFruitMod.MOD_ID, "main"),
        () -> VERSION, VERSION::equals, VERSION::equals
    );

    public static void register() {
        CHANNEL.registerMessage(id++, UseAbilityPacket.class,
            UseAbilityPacket::encode, UseAbilityPacket::new, UseAbilityPacket::handle);
    }

    public static <T> void sendToServer(T packet) {
        CHANNEL.sendToServer(packet);
    }
}
