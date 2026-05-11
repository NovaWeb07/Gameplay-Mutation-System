package com.longplayer.mod.golem;

import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.enderman.ModEntities;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GolemHitEvent {

    @SubscribeEvent
    public static void onGolemHit(LivingAttackEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof IronGolem original) {
            
            if (!(original instanceof HelicopterGolemEntity)) {
                
                HelicopterGolemEntity helicopter = ModEntities.HELICOPTER_GOLEM.get().create(original.level());
                if (helicopter != null) {
                    helicopter.moveTo(original.getX(), original.getY(), original.getZ(), original.getYRot(), original.getXRot());
                    helicopter.setHealth(original.getHealth()); 
                    
                    if (original.hasCustomName()) {
                        helicopter.setCustomName(original.getCustomName());
                        helicopter.setCustomNameVisible(original.isCustomNameVisible());
                    }
                    
                    original.level().addFreshEntity(helicopter);
                    original.discard(); 
                    
                    event.setCanceled(true); 
                    
                    helicopter.hurt(event.getSource(), event.getAmount());
                }
            }
        }
    }
}