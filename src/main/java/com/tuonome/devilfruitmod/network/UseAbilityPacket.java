package com.tuonome.devilfruitmod.network;

import com.tuonome.devilfruitmod.ability.AbilityHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class UseAbilityPacket {

    private final int slot; // 0=Logia, 1=Ab1, 2=Ab2, 3=Ab3

    public UseAbilityPacket(int slot) { this.slot = slot; }
    public UseAbilityPacket(FriendlyByteBuf buf) { this.slot = buf.readInt(); }
    public void encode(FriendlyByteBuf buf) { buf.writeInt(slot); }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            AbilityHandler.useAbility(player, slot);
        });
        ctx.get().setPacketHandled(true);
    }
}
