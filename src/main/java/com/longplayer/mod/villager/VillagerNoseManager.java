package com.longplayer.mod.villager;

import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.network.NetworkHandler;
import com.longplayer.mod.network.SyncVillagerNosePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerNoseManager {

    private static boolean serverIsActive = false;
    private static long serverStartTime = 0;

    private static boolean clientIsActive = false;
    private static long clientStartTime = 0;

    public static void enable(ServerPlayer player) {
        serverIsActive = true;
        serverStartTime = player.level().getGameTime();
        NetworkHandler.sendToAllClients(new SyncVillagerNosePacket(true, serverStartTime));
    }

    public static void disable(ServerPlayer player) {
        serverIsActive = false;
        serverStartTime = 0;
        NetworkHandler.sendToAllClients(new SyncVillagerNosePacket(false, 0));
    }

    public static void syncWithClients(ServerPlayer player) {
        NetworkHandler.sendToAllClients(new SyncVillagerNosePacket(serverIsActive, serverStartTime));
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof Villager villager) {
            if (serverIsActive) {
                long currentTick = villager.level().getGameTime();
                float computedLength = (currentTick - serverStartTime) * 0.02F;

                if (computedLength < 0.5F) return;

                double noseTipY = villager.getY() + 1.5 - computedLength;

                double groundY = villager.level().getHeight(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        (int) villager.getX(), (int) villager.getZ());

                if (noseTipY <= groundY) {
                    
                    double targetY = groundY + computedLength - 1.5;
                    double dy = targetY - villager.getY();

                    if (dy > 0) {
                        double velY = Math.min(dy, 0.4); 
                        villager.setDeltaMovement(villager.getDeltaMovement().x, velY, villager.getDeltaMovement().z);
                    } else {
                        
                        villager.setDeltaMovement(villager.getDeltaMovement().x, 0, villager.getDeltaMovement().z);
                    }
                    villager.fallDistance = 0;
                }
            }
        }
    }

    public static void setClientNose(boolean active, long startTime) {
        clientIsActive = active;
        clientStartTime = startTime;
    }

    public static boolean isClientActive() {
        return clientIsActive;
    }

    public static float getClientLength(long currentClientTick) {
        if (!clientIsActive) return 0.0F;
        return (currentClientTick - clientStartTime) * 0.02F;
    }
}