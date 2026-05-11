package com.longplayer.mod.client;

import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.data.LongPlayerData;
import com.longplayer.mod.data.SegmentPose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;

        for (Map.Entry<UUID, LongPlayerData> entry : LongPlayerData.getAllClientData().entrySet()) {
            UUID uuid = entry.getKey();
            LongPlayerData data = entry.getValue();

            for (Player p : mc.level.players()) {
                if (p.getUUID().equals(uuid)) {
                    data.recordPosition(p.getX(), p.getY(), p.getZ(), p.yBodyRot, p.getXRot());
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Vec3 camPos = event.getCamera().getPosition();
        float partialTick = event.getPartialTick();

        var poseStack = event.getPoseStack();
        var bufferSource = mc.renderBuffers().bufferSource();

        for (Map.Entry<UUID, LongPlayerData> entry : LongPlayerData.getAllClientData().entrySet()) {
            UUID uuid = entry.getKey();
            LongPlayerData data = entry.getValue();
            if (data.getSegmentCount() <= 0) continue;

            AbstractClientPlayer player = null;
            for (Player p : mc.level.players()) {
                if (p.getUUID().equals(uuid) && p instanceof AbstractClientPlayer acp) {
                    player = acp;
                    break;
                }
            }
            if (player == null) continue;

            double px = Mth.lerp(partialTick, player.xOld, player.getX());
            double py = Mth.lerp(partialTick, player.yOld, player.getY());
            double pz = Mth.lerp(partialTick, player.zOld, player.getZ());
            float pyRot = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot);
            float pxRot = Mth.lerp(partialTick, player.xRotO, player.getXRot());

            List<SegmentPose> poses = data.getSegmentPoses(px, py, pz, pyRot, pxRot);

            for (int i = 0; i < poses.size(); i++) {
                BodySegmentRenderer.renderSegment(
                    player, poseStack, bufferSource,
                    poses.get(i), camPos.x, camPos.y, camPos.z,
                    i, partialTick
                );
            }
        }

        bufferSource.endBatch();
    }
}