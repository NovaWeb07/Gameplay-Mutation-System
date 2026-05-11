package com.longplayer.mod.client;

import com.longplayer.mod.data.SegmentPose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;

public class BodySegmentRenderer {
    private static PlayerModel<AbstractClientPlayer> normalModel;
    private static PlayerModel<AbstractClientPlayer> slimModel;

    private static PlayerModel<AbstractClientPlayer> getModel(boolean slim) {
        if (slim) {
            if (slimModel == null) {
                slimModel = new PlayerModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true
                );
            }
            return slimModel;
        } else {
            if (normalModel == null) {
                normalModel = new PlayerModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false
                );
            }
            return normalModel;
        }
    }

    public static void renderSegment(
            AbstractClientPlayer player, PoseStack poseStack, MultiBufferSource bufferSource,
            SegmentPose pose, double camX, double camY, double camZ,
            int segmentIndex, float partialTick
    ) {
        boolean slim = "slim".equals(player.getModelName());
        PlayerModel<AbstractClientPlayer> model = getModel(slim);
        ResourceLocation skin = player.getSkinTextureLocation();

        poseStack.pushPose();

        poseStack.translate(pose.x - camX, pose.y - camY, pose.z - camZ);

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - pose.yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        float time = (float) Minecraft.getInstance().level.getGameTime() + partialTick;
        float limbSwing = time + segmentIndex * 1.5F;
        float limbSwingAmount = 0.8F;

        model.young = false;
        model.crouching = false;
        model.riding = false;
        model.attackTime = 0;

        model.setupAnim(player, limbSwing, limbSwingAmount, time, 0, 0);

        model.head.visible = false;
        model.hat.visible = false;
        model.leftArm.visible = false;
        model.rightArm.visible = false;
        model.leftSleeve.visible = false;
        model.rightSleeve.visible = false;
        model.body.visible = true;
        model.jacket.visible = true;
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.leftPants.visible = true;
        model.rightPants.visible = true;

        BlockPos lightPos = BlockPos.containing(pose.x, pose.y + 1, pose.z);
        int blockLight = 0, skyLight = 15;
        try {
            if (Minecraft.getInstance().level != null) {
                blockLight = Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, lightPos);
                skyLight = Minecraft.getInstance().level.getBrightness(LightLayer.SKY, lightPos);
            }
        } catch (Exception ignored) {}
        int packedLight = net.minecraft.client.renderer.LightTexture.pack(blockLight, skyLight);

        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucent(skin));
        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}