package com.longplayer.mod.villager;

import com.longplayer.mod.LongPlayerMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerClientEvents {
    private static HouseEntityModel houseModel;
    private static final ResourceLocation WOOD = new ResourceLocation("textures/block/oak_planks.png");
    private static final ResourceLocation COBBLE = new ResourceLocation("textures/block/cobblestone.png");

    @SubscribeEvent
    public static void onRenderVillagerPre(RenderLivingEvent.Pre<Villager, ?> event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (event.getEntity() instanceof BlockVillagerEntity) return;

        if (VillagerHouseManager.isClientEnabled) {
            event.setCanceled(true);
            renderHouseVillager(event, villager);
            return;
        }

        if (VillagerNoseManager.isClientActive()) {
            if (event.getRenderer() instanceof VillagerRenderer renderer) {
                try {
                    renderer.getModel().root().getChild("head").getChild("nose").visible = false;
                } catch (Exception ignored) {}
            }
        }
    }

    @SubscribeEvent
    public static void onRenderVillagerPost(RenderLivingEvent.Post<Villager, ?> event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (!VillagerNoseManager.isClientActive()) return;
        if (!(event.getRenderer() instanceof VillagerRenderer renderer)) return;

        try {
            renderer.getModel().root().getChild("head").getChild("nose").visible = true;
        } catch (Exception ignored) {}

        float lengthBlocks = VillagerNoseManager.getClientLength(villager.level().getGameTime());
        if (lengthBlocks <= 0.01F) return;

        VillagerModel<Villager> model = renderer.getModel();
        ModelPart head = model.root().getChild("head");
        ModelPart nose = head.getChild("nose");

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        int light = event.getPackedLight();
        float partialTick = event.getPartialTick();

        float bodyYaw = Mth.lerp(partialTick, villager.yBodyRotO, villager.yBodyRot);
        model.setupAnim(villager,
                villager.walkAnimation.position(),
                villager.walkAnimation.speed(),
                villager.tickCount + partialTick,
                Mth.lerp(partialTick, villager.yRotO, villager.getYRot()) - bodyYaw,
                Mth.lerp(partialTick, villager.xRotO, villager.getXRot()));

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        head.translateAndRotate(poseStack);

        poseStack.translate(nose.x / 16.0F, nose.y / 16.0F, nose.z / 16.0F);

        poseStack.mulPose(Axis.ZP.rotation(-head.zRot));
        poseStack.mulPose(Axis.YP.rotation(-head.yRot));
        poseStack.mulPose(Axis.XP.rotation(-head.xRot));

        poseStack.translate(0.0F, -1.0F / 16.0F, -6.0F / 16.0F);

        float hw = 1.0F / 16.0F;   
        float len = lengthBlocks;

        float u0 = 24.0F / 64.0F;
        float u1 = 26.0F / 64.0F;
        float v0 = 2.0F / 64.0F;
        float v1 = 6.0F / 64.0F;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(renderer.getTextureLocation(villager)));
        PoseStack.Pose pose = poseStack.last();

        addFace(vc, pose, hw, 0, -hw, hw, len, hw, 1, 0, 0, u0, v0, u1, v1, light);
        
        addFace(vc, pose, -hw, 0, hw, -hw, len, -hw, -1, 0, 0, u0, v0, u1, v1, light);
        
        addFace(vc, pose, -hw, 0, hw, hw, len, hw, 0, 0, 1, u0, v0, u1, v1, light);
        
        addFace(vc, pose, hw, 0, -hw, -hw, len, -hw, 0, 0, -1, u0, v0, u1, v1, light);

        poseStack.popPose();
    }

    private static void addFace(VertexConsumer vc, PoseStack.Pose pose,
                                 float ax, float ay, float az,
                                 float bx, float by, float bz,
                                 float nx, float ny, float nz,
                                 float u0, float v0, float u1, float v1,
                                 int light) {
        vc.vertex(pose.pose(), ax, ay, az).color(255, 255, 255, 255)
                .uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
        vc.vertex(pose.pose(), ax, by, az).color(255, 255, 255, 255)
                .uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
        vc.vertex(pose.pose(), bx, by, bz).color(255, 255, 255, 255)
                .uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
        vc.vertex(pose.pose(), bx, ay, bz).color(255, 255, 255, 255)
                .uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), nx, ny, nz).endVertex();
    }

    private static void renderHouseVillager(RenderLivingEvent.Pre<Villager, ?> event, Villager villager) {
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        int light = event.getPackedLight();
        float partialTick = event.getPartialTick();

        poseStack.pushPose();
        float yRot = Mth.lerp(partialTick, villager.yBodyRotO, villager.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        if (houseModel == null) {
            houseModel = new HouseEntityModel(Minecraft.getInstance().getEntityModels().bakeLayer(HouseEntityModel.LAYER_LOCATION));
        }
        houseModel.setupAnim(villager, villager.walkAnimation.position(), villager.walkAnimation.speed(),
                             villager.tickCount + partialTick, 0, 0);

        VertexConsumer cobbleVC = buffer.getBuffer(RenderType.entityCutout(COBBLE));
        houseModel.base.render(poseStack, cobbleVC, light, OverlayTexture.NO_OVERLAY);

        VertexConsumer woodVC = buffer.getBuffer(RenderType.entityCutout(WOOD));
        houseModel.roof.render(poseStack, woodVC, light, OverlayTexture.NO_OVERLAY);
        houseModel.leftLeg.render(poseStack, woodVC, light, OverlayTexture.NO_OVERLAY);
        houseModel.rightLeg.render(poseStack, woodVC, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}