package com.longplayer.mod.enderman;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FatEndermanEntity extends EnderMan {

    public FatEndermanEntity(EntityType<? extends FatEndermanEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EnderMan.createAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (this.getVehicle() instanceof Chicken chicken) {
            chicken.setNoGravity(true);
            chicken.fallDistance = 0;

            Vec3 motion = chicken.getDeltaMovement();

            double upSpeed = chicken.getY() < 120 ? 0.04D : 0.0D;

            chicken.setDeltaMovement(
                motion.x * 0.95 + (random.nextDouble() - 0.5) * 0.015,
                motion.y * 0.85 + upSpeed,
                motion.z * 0.95 + (random.nextDouble() - 0.5) * 0.015
            );

            if (random.nextInt(60) == 0) {
                chicken.setDeltaMovement(chicken.getDeltaMovement().add(
                    (random.nextDouble() - 0.5) * 0.4,
                    (random.nextDouble() - 0.3) * 0.1,
                    (random.nextDouble() - 0.5) * 0.4
                ));
            }

            chicken.setHealth(chicken.getMaxHealth());
        }
    }

    @Override
    public double getMyRidingOffset() {
        return -0.35D; 
    }

    @Override
    public boolean causeFallDamage(float dist, float mult, DamageSource source) {
        return false; 
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return false; 
    }
}