package com.longplayer.mod.enderman;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FatEndermanEyesLayer extends RenderLayer<FatEndermanEntity, FatEndermanModel<FatEndermanEntity>> {
    private static final RenderType EYES = RenderType.eyes(
        new ResourceLocation("textures/entity/enderman/enderman_eyes.png")
    );

    public FatEndermanEyesLayer(RenderLayerParent<FatEndermanEntity, FatEndermanModel<FatEndermanEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                        FatEndermanEntity entity, float limbSwing, float limbSwingAmount,
                        float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vc = buffer.getBuffer(EYES);
        this.getParentModel().renderToBuffer(poseStack, vc, 15728640,
            OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}