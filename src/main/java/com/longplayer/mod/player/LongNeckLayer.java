package com.longplayer.mod.player;

import com.longplayer.mod.LongPlayerMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import com.mojang.math.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LongNeckLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final ModelPart neckBox;

    public LongNeckLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
        
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        
        root.addOrReplaceChild("neck_segment", CubeListBuilder.create()
            .texOffs(12, 14)
            .addBox(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.ZERO);
            
        this.neckBox = LayerDefinition.create(mesh, 64, 64).bakeRoot().getChild("neck_segment");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        
        float visualLength = player.getPersistentData().getFloat("VisualNeckLength");
        if (visualLength <= 0.01f) return;

        int intLength = player.getPersistentData().getInt("LongNeckLength");

        poseStack.pushPose();

        float angle = 0f;
        if (intLength >= 7) {
            angle = player.getPersistentData().getFloat("LongNeckAngle");
        }
        
        poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
        
        if (intLength < 7) {
            poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(player.getSkinTextureLocation()));
        int overlay = net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

        poseStack.pushPose();
        
        poseStack.scale(5.0F, visualLength * 16.0F, 5.0F);
        
        this.neckBox.render(poseStack, consumer, light, overlay);
        poseStack.popPose();

        poseStack.translate(0, -visualLength, 0);
        
        PlayerModel<AbstractClientPlayer> model = this.getParentModel();
        
        float oldHeadX = model.head.xRot;
        float oldHeadY = model.head.yRot;
        
        model.head.xRot = 0;
        model.head.yRot = 0;
        model.hat.xRot = 0;
        model.hat.yRot = 0;
        
        model.head.visible = true;
        model.hat.visible = true;
        
        model.head.render(poseStack, consumer, light, overlay);
        model.hat.render(poseStack, consumer, light, overlay);
        
        model.head.visible = false;
        model.hat.visible = false;
        
        model.head.xRot = oldHeadX;
        model.head.yRot = oldHeadY;
        model.hat.xRot = oldHeadX;
        model.hat.yRot = oldHeadY;

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(skin -> {
            net.minecraft.client.renderer.entity.player.PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new LongNeckLayer(renderer));
            }
        });
    }
}