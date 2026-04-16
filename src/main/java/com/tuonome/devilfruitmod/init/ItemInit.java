package com.tuonome.devilfruitmod.init;

import com.tuonome.devilfruitmod.DevilFruitMod;
import com.tuonome.devilfruitmod.item.DevilFruitItem;
import com.tuonome.devilfruitmod.fruit.DevilFruit;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, DevilFruitMod.MOD_ID);

    public static final RegistryObject<Item> MAGU_MAGU_NO_MI =
        ITEMS.register("magu_magu_no_mi",
            () -> new DevilFruitItem(DevilFruit.MAGU));

    public static final RegistryObject<Item> HIE_HIE_NO_MI =
        ITEMS.register("hie_hie_no_mi",
            () -> new DevilFruitItem(DevilFruit.HIE));

    public static final RegistryObject<Item> GORO_GORO_NO_MI =
        ITEMS.register("goro_goro_no_mi",
            () -> new DevilFruitItem(DevilFruit.GORO));
}
