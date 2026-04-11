package com.tuonome.devilfruitmod.item;

import com.tuonome.devilfruitmod.fruit.DevilFruit;
import com.tuonome.devilfruitmod.fruit.DevilFruitSaveData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class DevilFruitItem extends Item {

    private final DevilFruit fruit;

    public DevilFruitItem(DevilFruit fruit) {
        super(new Properties().stacksTo(1));
        this.fruit = fruit;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.pass(player.getItemInHand(hand));

        ServerPlayer serverPlayer = (ServerPlayer) player;
        DevilFruitSaveData data = DevilFruitSaveData.get(serverPlayer.serverLevel());

        // Controlla se ha già un frutto
        if (data.hasFruit(serverPlayer.getUUID())) {
            serverPlayer.sendSystemMessage(Component.literal(
                "§cHai già mangiato un frutto del diavolo! §7(Solo uno per player)"
            ));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        // Mangia il frutto e ottieni i poteri
        data.setFruit(serverPlayer.getUUID(), fruit);
        player.getItemInHand(hand).shrink(1);

        serverPlayer.sendSystemMessage(Component.literal(
            "§aHai mangiato il §r" + fruit.displayName + "§a!"
        ));
        serverPlayer.sendSystemMessage(Component.literal(
            "§7" + fruit.subtitle
        ));
        serverPlayer.sendSystemMessage(Component.literal("§8--- Abilità sbloccate ---"));
        for (String desc : fruit.abilityDescriptions) {
            serverPlayer.sendSystemMessage(Component.literal(desc));
        }
        serverPlayer.sendSystemMessage(Component.literal(
            "§c⚠ §7Come tutti i Logia, perderai la capacità di nuotare!"
        ));

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(fruit.displayName));
        tooltip.add(Component.literal(fruit.subtitle));
        tooltip.add(Component.literal("§8Mangia per ottenere i poteri"));
        tooltip.add(Component.literal("§8Tipo: §7" + fruit.type.name()));
    }
}
