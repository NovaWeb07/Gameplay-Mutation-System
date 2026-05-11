package com.longplayer.mod.meat;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MeatSpawnEvent {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;

        boolean isTargetAnimal = entity instanceof Pig || entity instanceof Cow || entity instanceof Sheep || entity instanceof Chicken;
        if (!isTargetAnimal) return;

        ItemStack meatToSpawn = null;
        
        var iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemEntity drop = iterator.next();
            ItemStack stack = drop.getItem();
            
            if (stack.is(Items.PORKCHOP) || stack.is(Items.COOKED_PORKCHOP) ||
                stack.is(Items.BEEF) || stack.is(Items.COOKED_BEEF) ||
                stack.is(Items.MUTTON) || stack.is(Items.COOKED_MUTTON) ||
                stack.is(Items.CHICKEN) || stack.is(Items.COOKED_CHICKEN)) {
                
                if (meatToSpawn == null) meatToSpawn = stack.copy();
                else meatToSpawn.grow(stack.getCount());
                
                iterator.remove(); 
            }
        }

        if (meatToSpawn != null) {
            int count = meatToSpawn.getCount();
            ItemStack singleMeat = meatToSpawn.copy();
            singleMeat.setCount(1);
            
            for (int i = 0; i < count; i++) {
                LivingMeatEntity meatMonster = ModMeatEntities.LIVING_MEAT.get().create(entity.level());
                if (meatMonster != null) {
                    
                    double offsetX = (entity.level().random.nextDouble() - 0.5D) * 0.5D;
                    double offsetZ = (entity.level().random.nextDouble() - 0.5D) * 0.5D;
                    meatMonster.moveTo(entity.getX() + offsetX, entity.getY(), entity.getZ() + offsetZ, entity.getYRot(), entity.getXRot());
                    meatMonster.setMeatItem(singleMeat);
                    entity.level().addFreshEntity(meatMonster);
                }
            }
        }
    }
}