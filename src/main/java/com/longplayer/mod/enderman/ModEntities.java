package com.longplayer.mod.enderman;

import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.enderman.FatEndermanEntity;
import com.longplayer.mod.golem.HelicopterGolemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LongPlayerMod.MOD_ID);

    public static final RegistryObject<EntityType<FatEndermanEntity>> FAT_ENDERMAN =
        ENTITIES.register("fat_enderman", () -> EntityType.Builder
            .<FatEndermanEntity>of(FatEndermanEntity::new, MobCategory.CREATURE)
            .sized(0.8F, 1.2F)
            .clientTrackingRange(10)
            .build("fat_enderman"));

    public static final RegistryObject<EntityType<HelicopterGolemEntity>> HELICOPTER_GOLEM = ENTITIES.register("helicopter_golem",
            () -> EntityType.Builder.of(HelicopterGolemEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build("helicopter_golem"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}