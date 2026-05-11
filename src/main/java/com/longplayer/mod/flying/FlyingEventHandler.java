package com.longplayer.mod.flying;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlyingEventHandler {

    private static final Random RNG = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!FlyingManager.serverEnabled) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (Player player : level.players()) {
                AABB box = player.getBoundingBox().inflate(32);
                List<LivingEntity> mobs = level.getEntitiesOfClass(
                        LivingEntity.class, box,
                        e -> !(e instanceof Player)
                );
                for (LivingEntity mob : mobs) {
                    applyFlyingMadness(mob, level.getGameTime());
                }
            }
        }
    }

    private static void applyFlyingMadness(LivingEntity mob, long gameTime) {
        long id = mob.getId();
        
        long cycle = (gameTime + id * 13) % 20;
        long phase = id % 10;

        Vec3 vel = mob.getDeltaMovement();

        double yaw = Math.toRadians(mob.getYRot());
        
        double fwdX = -Math.sin(yaw);
        double fwdZ =  Math.cos(yaw);

        double drift = (Math.sin(gameTime * 0.05 + id * 2.7)) * 0.3;
        double driftX = -Math.sin(yaw + drift);
        double driftZ =  Math.cos(yaw + drift);

        if (cycle == phase % 20) {
            
            double upForce    = 0.5 + (RNG.nextDouble() * 0.5);   
            double fwdSpeed   = 0.5 + (RNG.nextDouble() * 0.4);   
            mob.setDeltaMovement(
                driftX * fwdSpeed,
                upForce,
                driftZ * fwdSpeed
            );
            mob.fallDistance = 0;
            
            mob.setYRot(mob.getYRot() + (float)(RNG.nextGaussian() * 20));
        } else if (cycle == (phase + 5) % 20) {
            
            if (!mob.onGround()) {
                mob.setDeltaMovement(
                    fwdX * 0.35,
                    Math.max(vel.y, -0.1), 
                    fwdZ * 0.35
                );
                mob.fallDistance = 0;
            }
        } else if (cycle == (phase + 12) % 20) {
            
            double sideYaw = yaw + Math.PI / 2;
            double sideX = -Math.sin(sideYaw) * 0.4;
            double sideZ =  Math.cos(sideYaw) * 0.4;
            mob.setDeltaMovement(
                sideX + fwdX * 0.3,
                vel.y + 0.15,
                sideZ + fwdZ * 0.3
            );
        }

        if (!mob.onGround()) {
            double carry = 0.08; 
            mob.setDeltaMovement(
                mob.getDeltaMovement().x + fwdX * carry,
                mob.getDeltaMovement().y,
                mob.getDeltaMovement().z + fwdZ * carry
            );
            mob.fallDistance = 0;
        }
    }

}