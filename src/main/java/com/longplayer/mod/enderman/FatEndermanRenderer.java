package com.longplayer.mod.enderman;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FatEndermanRenderer extends MobRenderer<FatEndermanEntity, FatEndermanModel<FatEndermanEntity>> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation("textures/entity/enderman/enderman.png");

    public FatEndermanRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FatEndermanModel<>(ctx.bakeLayer(FatEndermanModel.LAYER_LOCATION)), 0.4F);
        this.addLayer(new FatEndermanEyesLayer(this));
        this.addLayer(new FatEndermanCarriedBlockLayer(this, ctx.getBlockRenderDispatcher()));
    }

    @Override
    public void render(FatEndermanEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        FatEndermanModel<FatEndermanEntity> model = this.getModel();
        model.carrying = entity.getCarriedBlock() != null;
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FatEndermanEntity entity) {
        return TEXTURE;
    }
}