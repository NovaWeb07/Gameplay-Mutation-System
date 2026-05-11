package com.longplayer.mod.meat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.longplayer.mod.LongPlayerMod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LivingMeatEntity extends Monster {

    private static final EntityDataAccessor<ItemStack> DATA_MEAT_ITEM = SynchedEntityData.defineId(LivingMeatEntity.class, EntityDataSerializers.ITEM_STACK);

    public LivingMeatEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_MEAT_ITEM, new ItemStack(Items.PORKCHOP));
    }

    public ItemStack getMeatItem() {
        return this.entityData.get(DATA_MEAT_ITEM);
    }

    public void setMeatItem(ItemStack stack) {
        this.entityData.set(DATA_MEAT_ITEM, stack);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag itemTag = new CompoundTag();
        this.getMeatItem().save(itemTag);
        tag.put("MeatItem", itemTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("MeatItem")) {
            this.setMeatItem(ItemStack.of(tag.getCompound("MeatItem")));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModMeatEntities.LIVING_MEAT.get(), createAttributes().build());
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.world.damagesource.DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        ItemStack drop = this.getMeatItem().copy();
        if (this.isOnFire()) {
            if (drop.is(Items.PORKCHOP)) drop = new ItemStack(Items.COOKED_PORKCHOP, drop.getCount());
            else if (drop.is(Items.BEEF)) drop = new ItemStack(Items.COOKED_BEEF, drop.getCount());
            else if (drop.is(Items.MUTTON)) drop = new ItemStack(Items.COOKED_MUTTON, drop.getCount());
            else if (drop.is(Items.CHICKEN)) drop = new ItemStack(Items.COOKED_CHICKEN, drop.getCount());
            else if (drop.is(Items.RABBIT)) drop = new ItemStack(Items.COOKED_RABBIT, drop.getCount());
        }
        this.spawnAtLocation(drop);
    }
}