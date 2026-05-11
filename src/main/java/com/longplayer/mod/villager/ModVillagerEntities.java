package com.longplayer.mod.villager;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModVillagerEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LongPlayerMod.MOD_ID);

    public static final RegistryObject<EntityType<BlockVillagerEntity>> BLOCK_VILLAGER =
        ENTITIES.register("block_villager", () -> EntityType.Builder
            .<BlockVillagerEntity>of(BlockVillagerEntity::new, MobCategory.MISC)
            .sized(0.6F, 1.95F) 
            .clientTrackingRange(10)
            .build("block_villager"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}