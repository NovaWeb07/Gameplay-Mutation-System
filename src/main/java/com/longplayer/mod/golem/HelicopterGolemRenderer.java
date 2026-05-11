package com.longplayer.mod.golem;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.enderman.ModEntities;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HelicopterGolemRenderer extends MobRenderer<HelicopterGolemEntity, HelicopterGolemModel> {
    
    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("minecraft", "textures/entity/iron_golem/iron_golem.png");

    public HelicopterGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HelicopterGolemModel(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(HelicopterGolemEntity entity) {
        return GOLEM_LOCATION;
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HELICOPTER_GOLEM.get(), HelicopterGolemRenderer::new);
    }
}