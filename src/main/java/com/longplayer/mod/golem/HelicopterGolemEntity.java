package com.longplayer.mod.golem;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class HelicopterGolemEntity extends IronGolem {

    private static final EntityDataAccessor<Integer> STARTUP_TICKS = SynchedEntityData.defineId(HelicopterGolemEntity.class, EntityDataSerializers.INT);

    public HelicopterGolemEntity(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 64.0D) 
                .add(Attributes.FLYING_SPEED, 0.8F)
                .add(Attributes.MOVEMENT_SPEED, 0.4F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STARTUP_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Villager.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            int ticks = this.entityData.get(STARTUP_TICKS);
            if (ticks < 100) { 
                this.entityData.set(STARTUP_TICKS, ticks + 1);
                this.setDeltaMovement(0, 0, 0); 
            } else if (ticks == 100) {
                this.setNoGravity(true);
                this.entityData.set(STARTUP_TICKS, ticks + 1);
            }
            
            var target = this.getTarget();
            if (ticks >= 100) {
                if (target != null && target.isAlive()) {
                    double xDiff = target.getX() - this.getX();
                    double yDiff = target.getY() + target.getEyeHeight() - this.getY() - this.getEyeHeight();
                    double zDiff = target.getZ() - this.getZ();
                    net.minecraft.world.phys.Vec3 dir = new net.minecraft.world.phys.Vec3(xDiff, yDiff, zDiff).normalize();
                    
                    double diveSpeed = 0.12;
                    this.setDeltaMovement(this.getDeltaMovement().add(dir.x * diveSpeed, dir.y * diveSpeed, dir.z * diveSpeed));
                    
                    this.lookControl.setLookAt(target, 30.0F, 30.0F);
                } else {
                    int groundY = this.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, (int)this.getX(), (int)this.getZ());
                    if (this.getY() < groundY + 12.0) { 
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.05, 0)); 
                    } else if (this.getY() > groundY + 14.0) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.02, 0)); 
                    } else {
                        var currentMovement = this.getDeltaMovement();
                        this.setDeltaMovement(currentMovement.x * 0.95, currentMovement.y * 0.8, currentMovement.z * 0.95);
                    }
                }
            }
        }
    }

    public int getStartupTicks() {
        return this.entityData.get(STARTUP_TICKS);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.IRON_GOLEM_SPAWN_EGG);
    }
}