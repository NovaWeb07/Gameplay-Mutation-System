package com.longplayer.mod.enderman;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EndermanEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        EndermanCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;

        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());

        if (event.getTarget() instanceof Chicken chicken
                && held.getItem() == Items.ENDERMAN_SPAWN_EGG) {
            event.setCanceled(true);

            if (!player.isCreative()) {
                held.shrink(1);
            }

            ServerLevel level = (ServerLevel) event.getLevel();
            FatEndermanEntity enderman = ModEntities.FAT_ENDERMAN.get().create(level);
            if (enderman != null) {
                enderman.moveTo(chicken.getX(), chicken.getY(), chicken.getZ(),
                    chicken.getYRot(), 0);
                level.addFreshEntity(enderman);
                enderman.startRiding(chicken, true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(net.minecraftforge.event.entity.EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity().getType() == net.minecraft.world.entity.EntityType.ENDERMAN) {
            
            event.setCanceled(true);

            net.minecraft.world.entity.monster.EnderMan old = (net.minecraft.world.entity.monster.EnderMan) event.getEntity();
            ServerLevel level = (ServerLevel) event.getLevel();
            FatEndermanEntity fat = ModEntities.FAT_ENDERMAN.get().create(level);

            if (fat != null) {
                fat.moveTo(old.getX(), old.getY(), old.getZ(), old.getYRot(), old.getXRot());
                fat.setHealth(fat.getMaxHealth());
                
                if (old.getCarriedBlock() != null) {
                    fat.setCarriedBlock(old.getCarriedBlock());
                }
                level.addFreshEntity(fat);
            }
        }
    }
}