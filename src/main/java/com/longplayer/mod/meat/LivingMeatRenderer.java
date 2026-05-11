package com.longplayer.mod.meat;

import com.longplayer.mod.LongPlayerMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LivingMeatRenderer extends MobRenderer<LivingMeatEntity, LivingMeatModel> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(LongPlayerMod.MOD_ID, "living_meat"), "main");
    public static final ModelLayerLocation CHICKEN_LAYER = new ModelLayerLocation(new ResourceLocation(LongPlayerMod.MOD_ID, "living_meat_chicken"), "main");

    private static final ResourceLocation PIG_TEXTURE = new ResourceLocation("minecraft:textures/entity/pig/pig.png");
    private static final ResourceLocation COW_TEXTURE = new ResourceLocation("minecraft:textures/entity/cow/cow.png");
    private static final ResourceLocation SHEEP_TEXTURE = new ResourceLocation("minecraft:textures/entity/sheep/sheep.png");
    private static final ResourceLocation CHICKEN_TEXTURE = new ResourceLocation("minecraft:textures/entity/chicken.png");

    public LivingMeatRenderer(EntityRendererProvider.Context context) {
        super(context, new LivingMeatModel(context), 0.3F);
        this.addLayer(new MeatBodyLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(LivingMeatEntity entity) {
        ItemStack meat = entity.getMeatItem();
        if (meat.is(Items.BEEF) || meat.is(Items.COOKED_BEEF)) return COW_TEXTURE;
        if (meat.is(Items.MUTTON) || meat.is(Items.COOKED_MUTTON)) return SHEEP_TEXTURE;
        if (meat.is(Items.CHICKEN) || meat.is(Items.COOKED_CHICKEN)) return CHICKEN_TEXTURE;
        return PIG_TEXTURE;
    }

    public static class MeatBodyLayer extends RenderLayer<LivingMeatEntity, LivingMeatModel> {
        public MeatBodyLayer(RenderLayerParent<LivingMeatEntity, LivingMeatModel> parent) { 
            super(parent); 
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int light, LivingMeatEntity entity, float limbSwing, float limbSwingAmt, float partialTicks, float ageTicks, float netHeadYaw, float headPitch) {
            poseStack.pushPose();
            
            ItemStack meat = entity.getMeatItem();
            double yOffset = 0.9; 
            if (meat.is(Items.BEEF) || meat.is(Items.COOKED_BEEF)) yOffset = 0.65; 
            else if (meat.is(Items.MUTTON) || meat.is(Items.COOKED_MUTTON)) yOffset = 0.65; 
            else if (meat.is(Items.CHICKEN) || meat.is(Items.COOKED_CHICKEN)) yOffset = 1.05; 
            
            poseStack.translate(0.0, yOffset, 0.0); 
            poseStack.scale(1.5F, 1.5F, 1.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(180f)); 
            Minecraft.getInstance().getItemRenderer().renderStatic(
                entity.getMeatItem(), 
                ItemDisplayContext.FIXED, 
                light, 
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 
                poseStack, 
                buffer, 
                entity.level(), 
                entity.getId()
            );
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModMeatEntities.LIVING_MEAT.get(), LivingMeatRenderer::new);
    }
    
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER_LOCATION, LivingMeatModel::createQuadrupedLayer);
        event.registerLayerDefinition(CHICKEN_LAYER, LivingMeatModel::createChickenLayer);
    }
}