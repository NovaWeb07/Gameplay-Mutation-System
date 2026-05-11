package com.longplayer.mod.enderman;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;

public class FatEndermanCarriedBlockLayer extends RenderLayer<FatEndermanEntity, FatEndermanModel<FatEndermanEntity>> {
    private final BlockRenderDispatcher blockRenderer;

    public FatEndermanCarriedBlockLayer(RenderLayerParent<FatEndermanEntity, FatEndermanModel<FatEndermanEntity>> parent, BlockRenderDispatcher blockRenderer) {
        super(parent);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       FatEndermanEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        BlockState blockstate = entity.getCarriedBlock();
        if (blockstate != null) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.6875F, -0.75F);
            poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
            poseStack.translate(0.25F, 0.1875F, 0.25F);
            poseStack.scale(-0.5F, -0.5F, 0.5F);
            this.blockRenderer.renderSingleBlock(blockstate, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}