package com.longplayer.mod.wiggle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class WigglePlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public WigglePlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {

        if (!WiggleManager.clientEnabled) return;

        PlayerModel<AbstractClientPlayer> model = this.getParentModel();

        float t  = (player.level().getGameTime() + partialTick) * 0.08F;
        float ph = player.getId() * 1.618F;
        boolean crouching = player.isCrouching();
        boolean moving    = player.getDeltaMovement().horizontalDistanceSqr() > 0.001;

        float[] hX = {model.head.xRot,     model.head.yRot,     model.head.zRot};
        float[] hA = {model.hat.xRot,      model.hat.yRot,      model.hat.zRot};
        float[] bX = {model.body.xRot,     model.body.yRot,     model.body.zRot};
        float[] lA = {model.leftArm.xRot,  model.leftArm.yRot,  model.leftArm.zRot};
        float[] rA = {model.rightArm.xRot, model.rightArm.yRot, model.rightArm.zRot};
        float[] lL = {model.leftLeg.xRot,  model.leftLeg.yRot,  model.leftLeg.zRot};
        float[] rL = {model.rightLeg.xRot, model.rightLeg.yRot, model.rightLeg.zRot};

        model.head.yRot += (float) Math.sin(t * 2.5F + ph) * 0.70F;
        model.head.xRot += (float) Math.cos(t * 1.8F + ph) * 0.45F;
        model.head.zRot += (float) Math.sin(t * 1.2F + ph) * 0.35F;
        model.hat.xRot   = model.head.xRot;
        model.hat.yRot   = model.head.yRot;
        model.hat.zRot   = model.head.zRot;

        if (crouching) {
            model.body.xRot += (float) Math.sin(t * 4.0F + ph) * 0.55F;
            model.body.zRot += (float) Math.cos(t * 5.0F + ph) * 0.70F;
            model.body.yRot += (float) Math.sin(t * 3.0F + ph) * 0.40F;

            model.leftArm.xRot  += (float) Math.sin(t * 6.0F)               * 1.20F;
            model.leftArm.yRot  += (float) Math.cos(t * 7.0F)               * 1.40F;
            model.leftArm.zRot  += (float) Math.sin(t * 5.0F + 1.2F)       * 1.30F;
            model.rightArm.xRot += (float) Math.sin(t * 6.0F + 1.8F)       * 1.20F;
            model.rightArm.yRot += (float) Math.cos(t * 7.0F + 1.5F)       * 1.40F;
            model.rightArm.zRot += (float) Math.sin(t * 5.0F + 3.0F)       * 1.30F;

            model.leftLeg.xRot  += (float) Math.sin(t * 5.5F + ph)          * 0.65F;
            model.leftLeg.zRot  += (float) Math.cos(t * 4.5F + ph)          * 0.55F;
            model.rightLeg.xRot += (float) Math.sin(t * 5.5F + ph + 1.5F)   * 0.65F;
            model.rightLeg.zRot += (float) Math.cos(t * 4.5F + ph + 1.0F)   * 0.55F;

        } else if (moving) {
            model.body.xRot += (float) Math.sin(t * 0.7F + ph) * 0.20F;
            model.body.zRot += (float) Math.cos(t * 0.8F + ph) * 0.25F;

            model.leftArm.xRot  += (float) Math.sin(t * 2.5F + ph)          * 1.10F;
            model.leftArm.yRot  += (float) Math.cos(t * 2.0F + ph)          * 1.50F;
            model.leftArm.zRot  += (float) Math.sin(t * 1.5F + ph)          * 0.80F;
            model.rightArm.xRot += (float) Math.cos(t * 2.5F + ph + 1.0F)   * 1.10F;
            model.rightArm.yRot += (float) Math.sin(t * 2.0F + ph + 2.0F)   * 1.50F;
            model.rightArm.zRot += (float) Math.cos(t * 1.5F + ph + 1.5F)   * 0.80F;

            model.leftLeg.xRot  += (float) Math.sin(t * 1.8F + ph + 2.0F)   * 0.50F;
            model.leftLeg.zRot  += (float) Math.cos(t * 1.5F + ph)           * 0.30F;
            model.rightLeg.xRot += (float) Math.sin(t * 1.8F + ph)           * 0.50F;
            model.rightLeg.zRot += (float) Math.cos(t * 1.5F + ph + 1.5F)    * 0.30F;

        } else {
            model.body.xRot += (float) Math.sin(t * 0.7F + ph) * 0.12F;
            model.body.zRot += (float) Math.cos(t * 0.8F + ph) * 0.14F;

            model.leftArm.xRot  += (float) Math.sin(t * 1.8F + ph)          * 0.45F;
            model.leftArm.zRot  += (float) Math.cos(t * 1.4F + ph)          * 0.40F;
            model.rightArm.xRot += (float) Math.sin(t * 1.8F + ph + 1.0F)   * 0.45F;
            model.rightArm.zRot += (float) Math.cos(t * 1.4F + ph + 2.0F)   * 0.40F;

            model.leftLeg.xRot  += (float) Math.sin(t * 1.5F + ph + 2.0F)   * 0.25F;
            model.rightLeg.xRot += (float) Math.sin(t * 1.5F + ph)           * 0.25F;
        }

        model.head.xRot     = hX[0]; model.head.yRot     = hX[1]; model.head.zRot     = hX[2];
        model.hat.xRot      = hA[0]; model.hat.yRot      = hA[1]; model.hat.zRot      = hA[2];
        model.body.xRot     = bX[0]; model.body.yRot     = bX[1]; model.body.zRot     = bX[2];
        model.leftArm.xRot  = lA[0]; model.leftArm.yRot  = lA[1]; model.leftArm.zRot  = lA[2];
        model.rightArm.xRot = rA[0]; model.rightArm.yRot = rA[1]; model.rightArm.zRot = rA[2];
        model.leftLeg.xRot  = lL[0]; model.leftLeg.yRot  = lL[1]; model.leftLeg.zRot  = lL[2];
        model.rightLeg.xRot = rL[0]; model.rightLeg.yRot = rL[1]; model.rightLeg.zRot = rL[2];
    }
}