package com.longplayer.mod.villager;

import com.longplayer.mod.LongPlayerMod;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class VillagerPartStealer {

    public static final String NBT_STOLEN_PARTS = "StolenParts";
    public static final String NBT_PLAYER_STOLEN_PARTS_LIST = "StolenVillagerPartsList";

    @Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            if (event.getTarget() instanceof Villager villager) {
                Player player = event.getEntity();
                InteractionHand hand = event.getHand();
                if (hand != InteractionHand.MAIN_HAND) return;

                int villagerParts = villager.getPersistentData().getInt(NBT_STOLEN_PARTS);
                if (villagerParts < 6) {
                    villagerParts++;
                    villager.getPersistentData().putInt(NBT_STOLEN_PARTS, villagerParts);

                    net.minecraft.nbt.ListTag list = player.getPersistentData().getList(NBT_PLAYER_STOLEN_PARTS_LIST, 10);
                    net.minecraft.nbt.CompoundTag newPart = new net.minecraft.nbt.CompoundTag();
                    newPart.putInt("PartId", villagerParts);

                    if (villagerParts == 1) { 
                        boolean hasNose = false;
                        for (int i = 0; i < list.size(); i++) {
                            if (list.getCompound(i).getInt("PartId") == 1) {
                                hasNose = true; break;
                            }
                        }
                        newPart.putBoolean("IsFirstNose", !hasNose);
                    } else {
                        net.minecraft.util.RandomSource rand = player.getRandom();
                        newPart.putFloat("OffsetX", rand.nextFloat() * 20f - 10f); 
                        newPart.putFloat("OffsetY", rand.nextFloat() * 24f - 8f); 
                        newPart.putFloat("OffsetZ", rand.nextFloat() * 20f - 10f);
                        newPart.putFloat("RotX", rand.nextFloat() * (float)Math.PI * 2);
                        newPart.putFloat("RotY", rand.nextFloat() * (float)Math.PI * 2);
                        newPart.putFloat("RotZ", rand.nextFloat() * (float)Math.PI * 2);
                    }
                    list.add(newPart);
                    player.getPersistentData().put(NBT_PLAYER_STOLEN_PARTS_LIST, list);

                    villager.level().playSound(null, villager.blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    if (villagerParts == 6 && !villager.level().isClientSide()) {
                        villager.kill();
                    }

                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void setVisibleSafe(ModelPart root, boolean visible, String... paths) {
        ModelPart current = root;
        for (String p : paths) {
            try {
                current = current.getChild(p);
            } catch (Exception e) {
                return; 
            }
        }
        if (current != null) {
            current.visible = visible;
        }
    }

    @Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onPreRender(RenderLivingEvent.Pre<?, ?> event) {
            if (event.getEntity() instanceof Villager villager && event.getRenderer().getModel() instanceof VillagerModel<?> model) {
                int parts = villager.getPersistentData().getInt(NBT_STOLEN_PARTS);
                if (parts > 0) {
                    ModelPart root = model.root();
                    if (parts >= 1) setVisibleSafe(root, false, "head", "nose");
                    if (parts >= 2) setVisibleSafe(root, false, "body", "jacket");
                    if (parts >= 3) {
                        setVisibleSafe(root, false, "right_leg");
                        setVisibleSafe(root, false, "left_leg");
                    }
                    if (parts >= 4) setVisibleSafe(root, false, "arms");
                    if (parts >= 5) setVisibleSafe(root, false, "body");
                    if (parts >= 6) {
                        setVisibleSafe(root, false, "head");
                        setVisibleSafe(root, false, "head", "hat");
                        setVisibleSafe(root, false, "head", "hat_rim");
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onPostRender(RenderLivingEvent.Post<?, ?> event) {
            if (event.getEntity() instanceof Villager villager && event.getRenderer().getModel() instanceof VillagerModel<?> model) {
                int parts = villager.getPersistentData().getInt(NBT_STOLEN_PARTS);
                if (parts > 0) {
                    ModelPart root = model.root();
                    setVisibleSafe(root, true, "head", "nose");
                    setVisibleSafe(root, true, "body", "jacket");
                    setVisibleSafe(root, true, "right_leg");
                    setVisibleSafe(root, true, "left_leg");
                    setVisibleSafe(root, true, "arms");
                    setVisibleSafe(root, true, "body");
                    setVisibleSafe(root, true, "head");
                    setVisibleSafe(root, true, "head", "hat");
                    setVisibleSafe(root, true, "head", "hat_rim");
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
            VillagerModel<Villager> defaultModel = new VillagerModel<>(event.getEntityModels().bakeLayer(ModelLayers.VILLAGER));
            
            for (String skinName : event.getSkins()) {
                var renderer = event.getSkin(skinName);
                if (renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer) {
                    @SuppressWarnings("unchecked")
                    net.minecraft.client.renderer.entity.LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> playerRenderer = 
                        (net.minecraft.client.renderer.entity.LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) renderer;
                    playerRenderer.addLayer(new VillagerPartsPlayerLayer(playerRenderer, defaultModel));
                }
            }
        }
    }

    private static ModelPart getSafe(ModelPart root, String... paths) {
        ModelPart current = root;
        for (String p : paths) {
            try {
                current = current.getChild(p);
            } catch (Exception e) {
                return null;
            }
        }
        return current;
    }

    public static class VillagerPartsPlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
        private final VillagerModel<Villager> villagerModel;
        private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("minecraft:textures/entity/villager/villager.png");

        public VillagerPartsPlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, VillagerModel<Villager> villagerModel) {
            super(renderer);
            this.villagerModel = villagerModel;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            net.minecraft.nbt.ListTag list = player.getPersistentData().getList(NBT_PLAYER_STOLEN_PARTS_LIST, 10);
            if (list.isEmpty()) return;
            
            PlayerModel<AbstractClientPlayer> playerModel = this.getParentModel();
            com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(VILLAGER_LOCATION));
            ModelPart root = villagerModel.root();

            for (int i = 0; i < list.size(); i++) {
                net.minecraft.nbt.CompoundTag tag = list.getCompound(i);
                int partId = tag.getInt("PartId");
                boolean isFirstNose = tag.getBoolean("IsFirstNose");
                
                poseStack.pushPose();

                if (partId == 1 && isFirstNose) {
                    playerModel.head.translateAndRotate(poseStack);
                    
                } else if (partId == 1 && !isFirstNose) {
                    
                    playerModel.body.translateAndRotate(poseStack);
                    poseStack.translate(0, 10f/16f, 0); 
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180f));  
                    poseStack.translate(0, 0, 4f/16f);
                } else {
                    
                    playerModel.body.translateAndRotate(poseStack);
                    float dx = tag.getFloat("OffsetX");
                    float dy = tag.getFloat("OffsetY");
                    float dz = tag.getFloat("OffsetZ");
                    float rx = tag.getFloat("RotX");
                    float ry = tag.getFloat("RotY");
                    float rz = tag.getFloat("RotZ");
                    poseStack.translate(dx / 16f, dy / 16f, dz / 16f);
                    poseStack.mulPose(com.mojang.math.Axis.XP.rotation(rx));
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotation(ry));
                    poseStack.mulPose(com.mojang.math.Axis.ZP.rotation(rz));
                }

                if (partId == 1) {
                    ModelPart nose = getSafe(root, "head", "nose");
                    if (nose != null) nose.render(poseStack, vertexConsumer, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                } else if (partId == 2) {
                    ModelPart jacket = getSafe(root, "body", "jacket");
                    if (jacket == null) jacket = getSafe(root, "jacket");
                    if (jacket != null) jacket.render(poseStack, vertexConsumer, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                } else if (partId == 3) {
                    ModelPart rightLeg = getSafe(root, "right_leg");
                    ModelPart leftLeg = getSafe(root, "left_leg");
                    if (rightLeg != null) rightLeg.render(poseStack, vertexConsumer, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    if (leftLeg != null) leftLeg.render(poseStack, vertexConsumer, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                } else if (partId == 4) {
                    ModelPart arms = getSafe(root, "arms");
                    if (arms != null) arms.render(poseStack, vertexConsumer, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                } else if (partId == 5) {
                    ModelPart body = getSafe(root, "body");
                    if (body != null) body.render(poseStack, vertexConsumer, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                } else if (partId == 6) {
                    ModelPart head = getSafe(root, "head");
                    if (head != null) {
                        
                        boolean p1 = false, p2 = false;
                        ModelPart n = getSafe(root, "head", "nose");
                        if (n != null) { p1 = n.visible; n.visible = true; }
                        head.render(poseStack, vertexConsumer, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        if (n != null) { n.visible = p1; }
                    }
                }

                poseStack.popPose();
            }
        }
    }
}