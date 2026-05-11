package com.longplayer.mod.enderman;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, LongPlayerMod.MOD_ID);

    public static final RegistryObject<Item> FAT_ENDERMAN_SPAWN_EGG =
        ITEMS.register("fat_enderman_spawn_egg", () -> new ForgeSpawnEggItem(
            ModEntities.FAT_ENDERMAN, 0x161616, 0xCC0000, new Item.Properties() 
        ));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}