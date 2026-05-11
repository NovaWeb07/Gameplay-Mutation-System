package com.longplayer.mod.enderman;

import com.longplayer.mod.LongPlayerMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EndermanModEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.FAT_ENDERMAN.get(), FatEndermanEntity.createAttributes().build());
        event.put(ModEntities.HELICOPTER_GOLEM.get(), com.longplayer.mod.golem.HelicopterGolemEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FAT_ENDERMAN.get(), FatEndermanRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FatEndermanModel.LAYER_LOCATION, FatEndermanModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void buildContents(net.minecraftforge.event.BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.FAT_ENDERMAN_SPAWN_EGG);
        }
    }
}