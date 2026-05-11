package com.longplayer.mod.player;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LongNeckEventHooks {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handleEatGrass(event.getEntity(), event.getItemStack(), event);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        handleEatGrass(event.getEntity(), event.getItemStack(), event);
    }

    private static void handleEatGrass(Player player, ItemStack stack, PlayerInteractEvent event) {
        if (stack.is(Items.GRASS_BLOCK)) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            player.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
            
            if (player.isShiftKeyDown()) {
                player.getPersistentData().putBoolean("NeckHandSupport", true);
            }

            int len = player.getPersistentData().getInt("LongNeckLength");
            if (len < 7) {
                player.getPersistentData().putInt("LongNeckLength", len + 1);
                if (!player.level().isClientSide()) {
                    LongNeckNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), 
                        new LongNeckNetwork.SyncNeckPacket(player.getId(), len + 1));
                }
            }
            player.swing(InteractionHand.MAIN_HAND);
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        int len = event.getOriginal().getPersistentData().getInt("LongNeckLength");
        event.getEntity().getPersistentData().putInt("LongNeckLength", len);
    }
    
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player target) {
            int len = target.getPersistentData().getInt("LongNeckLength");
            LongNeckNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), 
                new LongNeckNetwork.SyncNeckPacket(target.getId(), len));
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        int len = player.getPersistentData().getInt("LongNeckLength");
        LongNeckNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), 
            new LongNeckNetwork.SyncNeckPacket(player.getId(), len));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.END) {
            boolean hasGrass = false;
            for (ItemStack stack : player.getInventory().items) {
                if (stack.is(Items.GRASS_BLOCK)) {
                    hasGrass = true;
                    break;
                }
            }
            if (!hasGrass) {
                player.getPersistentData().putInt("LongNeckLength", 0);
                player.getPersistentData().putBoolean("NeckHandSupport", false);
            }

            if (event.side.isClient()) {
                int length = player.getPersistentData().getInt("LongNeckLength");
                
                float visualLength = player.getPersistentData().getFloat("VisualNeckLength");
                if (length == 0) {
                    visualLength = 0f; 
                    player.getPersistentData().putFloat("VisualNeckLength", 0f);
                } else if (visualLength < length) {
                    visualLength += 0.05f; 
                    if (visualLength > length) visualLength = length;
                    player.getPersistentData().putFloat("VisualNeckLength", visualLength);
                } else if (visualLength > length) {
                    visualLength -= 0.15f; 
                    if (visualLength < length) visualLength = length;
                    player.getPersistentData().putFloat("VisualNeckLength", visualLength);
                }

                if (length >= 7) {
                float angle = player.getPersistentData().getFloat("LongNeckAngle");
                float vel = player.getPersistentData().getFloat("LongNeckVelocity");

                vel += (float) Math.sin(Math.toRadians(angle)) * 1.5f; 
                
                float targetAngle = player.getXRot(); 
                float diff = targetAngle - angle;
                vel += diff * 0.1f; 
                
                vel *= 0.85f; 
                angle += vel;

                if (angle > 90.0f) angle = 90.0f;
                if (angle < -90.0f) angle = -90.0f;

                player.getPersistentData().putFloat("LongNeckAngle", angle);
                player.getPersistentData().putFloat("LongNeckVelocity", vel);
            } else {
                player.getPersistentData().putFloat("LongNeckAngle", 0f);
                player.getPersistentData().putFloat("LongNeckVelocity", 0f);
            }
        }
        }
    }

    @Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientHooks {
        @SubscribeEvent
        public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
            Player player = event.getEntity();
            float visualLength = player.getPersistentData().getFloat("VisualNeckLength");
            if (visualLength > 0.1f) {
                PlayerModel<?> model = event.getRenderer().getModel();
                model.head.visible = false;
                model.hat.visible = false;

                if (visualLength >= 3.0f && player.getPersistentData().getBoolean("NeckHandSupport")) {
                    
                    model.leftArm.xRot = (float) Math.toRadians(-180);
                    model.leftArm.yRot = (float) Math.toRadians(30);
                    model.leftArm.zRot = (float) Math.toRadians(-25); 
                    model.leftSleeve.copyFrom(model.leftArm); 
                    
                    if (player.getDeltaMovement().lengthSqr() > 0.01) {
                        model.rightLeg.xRot *= 1.5f;
                        model.leftLeg.xRot *= 1.5f;
                        model.rightLeg.zRot = (float) Math.sin(player.tickCount * 0.5) * 0.2f;
                        model.leftLeg.zRot = (float) Math.cos(player.tickCount * 0.5) * 0.2f;
                    }
                }
            } else {
                event.getRenderer().getModel().head.visible = true;
                event.getRenderer().getModel().hat.visible = true;
            }
        }
    }
}