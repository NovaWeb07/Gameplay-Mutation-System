package com.longplayer.mod.meat;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMeatEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LongPlayerMod.MOD_ID);

    public static final RegistryObject<EntityType<LivingMeatEntity>> LIVING_MEAT = ENTITIES.register("living_meat",
        () -> EntityType.Builder.<LivingMeatEntity>of(LivingMeatEntity::new, MobCategory.MONSTER)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .build("living_meat"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}