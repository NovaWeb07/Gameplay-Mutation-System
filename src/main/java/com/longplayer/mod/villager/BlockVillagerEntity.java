package com.longplayer.mod.villager;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class BlockVillagerEntity extends Mob {

    public BlockVillagerEntity(EntityType<? extends BlockVillagerEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setSilent(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide && !VillagerHouseManager.isEnabled) {
            this.discard();
        }
    }

    @Override
    public boolean isNoAi() {
        return true; 
    }
    
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return false; 
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }
}