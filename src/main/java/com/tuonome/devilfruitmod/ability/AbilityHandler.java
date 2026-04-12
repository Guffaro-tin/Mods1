package com.tuonome.devilfruitmod.ability;

import com.tuonome.devilfruitmod.DevilFruitMod;
import com.tuonome.devilfruitmod.fruit.DevilFruit;
import com.tuonome.devilfruitmod.fruit.DevilFruitSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = DevilFruitMod.MOD_ID)
public class AbilityHandler {

    private static final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    // ─── TICK: effetti passivi ogni secondo ───────────────────────

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.tickCount % 20 != 0) return;

        DevilFruitSaveData data = DevilFruitSaveData.get(player.serverLevel());
        DevilFruit fruit = data.getFruit(player.getUUID());
        if (fruit == null) return;

        switch (fruit) {
            case MAGU -> tickMagu(player, data);
            case HIE  -> tickHie(player, data);
            case GORO -> tickGoro(player, data);
        }
    }

    private static void tickMagu(ServerPlayer player, DevilFruitSaveData data) {
        // Immunità fuoco permanente
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, false, false));
        // Logia attiva: danno fuoco ai nemici vicini
        if (data.isLogiaActive(player.getUUID())) {
            player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(1.5))
                .stream().filter(e -> e != player)
                .forEach(e -> e.setSecondsOnFire(3));
        }
    }

    private static void tickHie(ServerPlayer player, DevilFruitSaveData data) {
        // Frost Walker: congela acqua sotto i piedi
        BlockPos below = player.blockPosition().below();
        if (player.level().getBlockState(below).is(Blocks.WATER))
            player.level().setBlock(below, Blocks.ICE.defaultBlockState(), 3);

        // Logia attiva: rallenta nemici vicini
        if (data.isLogiaActive(player.getUUID())) {
            player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(1.5))
                .stream().filter(e -> e != player)
                .forEach(e -> e.addEffect(
                    new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 3, false, true)));
        }
    }

    private static void tickGoro(ServerPlayer player, DevilFruitSaveData data) {
        // Logia attiva: velocità + scossa elettrica ai nemici vicini
        if (data.isLogiaActive(player.getUUID())) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
            player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(1.5))
                .stream().filter(e -> e != player)
                .forEach(e -> {
                    e.hurt(player.damageSources().lightning(), 2.0f);
                    e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, true));
                });
        }
    }

    // ─── LOGIA: invulnerabilità ai danni fisici ───────────────────

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        DevilFruitSaveData data = DevilFruitSaveData.get(player.serverLevel());
        if (!data.isLogiaActive(player.getUUID())) return;
        DevilFruit fruit = data.getFruit(player.getUUID());
        if (fruit == null) return;

        // Logia: invulnerabile agli attacchi fisici normali
        if (!event.getSource().isBypassInvul() && !event.getSource().isFire()) {
            event.setCanceled(true);
        }
    }

    // ─── ABILITÀ: chiamate dai tasti R/Z/X/C ─────────────────────

    public static void useAbility(ServerPlayer player, int slot) {
        DevilFruitSaveData data = DevilFruitSaveData.get(player.serverLevel());
        DevilFruit fruit = data.getFruit(player.getUUID());
        if (fruit == null) {
            player.sendSystemMessage(Component.literal("§cNon hai nessun frutto del diavolo!"));
            return;
        }
        switch (slot) {
            case 0 -> toggleLogia(player, data, fruit);
            case 1 -> useAbility1(player, data, fruit);
            case 2 -> useAbility2(player, data, fruit);
            case 3 -> useAbility3(player, data, fruit);
        }
    }

    private static void toggleLogia(ServerPlayer player, DevilFruitSaveData data, DevilFruit fruit) {
        boolean active = data.isLogiaActive(player.getUUID());
        data.setLogiaActive(player.getUUID(), !active);
        if (!active) {
            player.sendSystemMessage(Component.literal(fruit.displayName + " §aLogia ATTIVA 🔥"));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LAVA_AMBIENT, SoundSource.PLAYERS, 0.5f, 1.2f);
        } else {
            player.sendSystemMessage(Component.literal(fruit.displayName + " §cLogia DISATTIVA"));
        }
    }

    private static void useAbility1(ServerPlayer player, DevilFruitSaveData data, DevilFruit fruit) {
        if (!checkCooldown(player.getUUID(), "ab1", 5000)) {
            player.sendSystemMessage(Component.literal("§cCooldown! Aspetta qualche secondo."));
            return;
        }
        switch (fruit) {
            case MAGU -> {
                // Lava Flow: crea lava in area 3x3
                BlockPos pos = player.blockPosition().below();
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++) {
                        BlockPos t = pos.offset(i, 0, j);
                        if (player.level().getBlockState(t).isAir())
                            player.level().setBlock(t, Blocks.LAVA.defaultBlockState(), 3);
                    }
                player.sendSystemMessage(Component.literal("§c🌋 Lava Flow!"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LAVA_POP, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
            case HIE -> {
                // Frost Walker: congela area 5x5
                BlockPos pos2 = player.blockPosition();
                for (int i = -2; i <= 2; i++)
                    for (int j = -2; j <= 2; j++) {
                        BlockPos t = pos2.offset(i, -1, j);
                        if (player.level().getBlockState(t).is(Blocks.WATER))
                            player.level().setBlock(t, Blocks.ICE.defaultBlockState(), 3);
                    }
                player.sendSystemMessage(Component.literal("§b❄ Frost Walker!"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 1.5f);
            }
            case GORO -> {
                // Spark Step: teletrasporto offensivo 8 blocchi in avanti
                Vec3 look = player.getLookAngle();
                player.teleportTo(
                    player.getX() + look.x * 8,
                    player.getY(),
                    player.getZ() + look.z * 8);
                player.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(2.0))
                    .stream().filter(e -> e != player)
                    .forEach(e -> {
                        e.hurt(player.damageSources().lightning(), 6.0f);
                        e.setSecondsOnFire(2);
                    });
                player.sendSystemMessage(Component.literal("§e⚡ Spark Step!"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.5f, 1.5f);
            }
        }
        setCooldown(player.getUUID(), "ab1");
    }

    private static void useAbility2(ServerPlayer player, DevilFruitSaveData data, DevilFruit fruit) {
        if (!checkCooldown(player.getUUID(), "ab2", 10000)) {
            player.sendSystemMessage(Component.literal("§cCooldown! Aspetta 10 secondi."));
            return;
        }
        switch (fruit) {
            case MAGU -> {
                // Magma Coating: armatura di magma 10 secondi
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 1, false, true));
                player.sendSystemMessage(Component.literal("§c🔥 Magma Coating! (10 sec)"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LAVA_POP, SoundSource.PLAYERS, 1.0f, 0.5f);
            }
            case HIE -> {
                // Ice Saber: forza + rallenta nemico più vicino
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 2, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, true));
                player.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(5.0))
                    .stream().filter(e -> e != player)
                    .min(Comparator.comparingDouble(e -> e.distanceTo(player)))
                    .ifPresent(e -> e.addEffect(
                        new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 5, false, true)));
                player.sendSystemMessage(Component.literal("§b🗡 Ice Saber!"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
            case GORO -> {
                // El Thor: fulmine sul nemico più vicino entro 20 blocchi
                player.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(20.0))
                    .stream().filter(e -> e != player)
                    .min(Comparator.comparingDouble(e -> e.distanceTo(player)))
                    .ifPresent(e -> {
                        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                        bolt.moveTo(e.getX(), e.getY(), e.getZ());
                        ((ServerLevel) player.level()).addFreshEntity(bolt);
                        e.hurt(player.damageSources().lightning(), 15.0f);
                    });
                player.sendSystemMessage(Component.literal("§e⚡ El Thor!"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
        }
        setCooldown(player.getUUID(), "ab2");
    }

    private static void useAbility3(ServerPlayer player, DevilFruitSaveData data, DevilFruit fruit) {
        if (!checkCooldown(player.getUUID(), "ab3", 30000)) {
            player.sendSystemMessage(Component.literal("§cCooldown! Aspetta 30 secondi."));
            return;
        }
        switch (fruit) {
            case MAGU -> {
                // Ryusei Kazan: 8 palle di fuoco
                player.sendSystemMessage(Component.literal("§c☄ Ryusei Kazan!"));
                for (int i = 0; i < 8; i++) {
                    double ox = (Math.random() - 0.5) * 20;
                    double oz = (Math.random() - 0.5) * 20;
                    net.minecraft.world.entity.projectile.LargeFireball fireball =
                        new net.minecraft.world.entity.projectile.LargeFireball(
                            player.level(), player, ox * 0.1, -1.0, oz * 0.1, 3);
                    fireball.moveTo(player.getX() + ox, player.getY() + 30, player.getZ() + oz);
                    player.level().addFreshEntity(fireball);
                }
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LAVA_POP, SoundSource.PLAYERS, 2.0f, 0.3f);
            }
            case HIE -> {
                // Ice Age: congela area 15 blocchi + danni freddo
                BlockPos center = player.blockPosition();
                int r = 15;
                for (int x = -r; x <= r; x++)
                    for (int z = -r; z <= r; z++)
                        if (x*x + z*z <= r*r) {
                            BlockPos t = center.offset(x, -1, z);
                            if (player.level().getBlockState(t).is(Blocks.WATER))
                                player.level().setBlock(t, Blocks.ICE.defaultBlockState(), 3);
                        }
                player.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(r))
                    .stream().filter(e -> e != player)
                    .forEach(e -> {
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 10, false, true));
                        e.hurt(player.damageSources().freeze(), 10.0f);
                    });
                player.sendSystemMessage(Component.literal("§b❄ Ice Age! (raggio 15)"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 2.0f, 0.3f);
            }
            case GORO -> {
                // Raigo: fulmine su tutti i nemici in raggio 25
                player.level().getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(25.0))
                    .stream().filter(e -> e != player)
                    .forEach(e -> {
                        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                        bolt.moveTo(e.getX(), e.getY(), e.getZ());
                        ((ServerLevel) player.level()).addFreshEntity(bolt);
                        e.hurt(player.damageSources().lightning(), 25.0f);
                        e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, true));
                    });
                player.sendSystemMessage(Component.literal("§e⚡ Raigo! (raggio 25)"));
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 0.5f);
            }
        }
        setCooldown(player.getUUID(), "ab3");
    }

    // ─── Utility cooldown ─────────────────────────────────────────

    private static boolean checkCooldown(UUID uuid, String ab, long ms) {
        return System.currentTimeMillis() -
            cooldowns.getOrDefault(uuid, new HashMap<>()).getOrDefault(ab, 0L) >= ms;
    }

    private static void setCooldown(UUID uuid, String ab) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>())
            .put(ab, System.currentTimeMillis());
    }
}
