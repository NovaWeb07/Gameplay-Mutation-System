package com.longplayer.mod.wiggle;

import com.longplayer.mod.LongPlayerMod;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WiggleClientEvents {

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (!WiggleManager.clientEnabled) return;
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        float t     = (mc.level.getGameTime() + event.getPartialTick()) * 0.1F;
        float phase = mc.player.getId() * 1.618F;

        var ps = event.getPoseStack();

        float wobbleX = (float) Math.sin(t * 3.0F + phase) * 0.09F;
        float wobbleY = (float) Math.cos(t * 2.2F + phase) * 0.09F;
        float spinZ   = (float) Math.sin(t * 1.5F + phase) * 0.12F;

        ps.translate(wobbleX, wobbleY, 0);
        ps.mulPose(Axis.XP.rotation(wobbleY * 2.0F));
        ps.mulPose(Axis.ZP.rotation(spinZ));
        ps.mulPose(Axis.YP.rotation(wobbleX * 1.5F));
    }

    @SubscribeEvent
    public static void onMobRenderPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!WiggleManager.clientEnabled) return;
        if (event.getEntity() instanceof Player) return;  

        LivingEntity entity   = event.getEntity();
        float t               = (entity.level().getGameTime() + event.getPartialTick()) * 0.08F;
        boolean isVillager    = entity instanceof Villager;

        EntityModel<?> model  = event.getRenderer().getModel();
        applyMobWiggle(model, t, entity.getId(), isVillager);
    }

    @SubscribeEvent
    public static void onMobRenderPost(RenderLivingEvent.Post<?, ?> event) {
        
        if (event.getEntity() instanceof Player) return;
        if (!parts.isEmpty()) restoreAll();
    }

    @SubscribeEvent
    public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        if (!WiggleManager.clientEnabled) return;

        Player player   = event.getEntity();
        float t         = (player.level().getGameTime() + event.getPartialTick()) * 0.08F;
        float ph        = player.getId() * 1.618F;
        boolean crouch  = player.isCrouching();
        boolean moving  = player.getDeltaMovement().horizontalDistanceSqr() > 0.001;

        var ps = event.getPoseStack();

        float scaleY = 1.0F + (float) Math.sin(t * 2.0F + ph) * (crouch ? 0.20F : 0.10F);
        float scaleX = 1.0F + (float) Math.cos(t * 1.7F + ph) * (crouch ? 0.15F : 0.07F);
        float scaleZ = 1.0F + (float) Math.sin(t * 1.3F + ph) * 0.06F;

        float tiltX = (float) Math.sin(t * (moving ? 2.5F : 1.5F) + ph) * (crouch ? 0.28F : 0.14F);
        float tiltZ = (float) Math.cos(t * (moving ? 2.0F : 1.2F) + ph) * (crouch ? 0.32F : 0.16F);

        float bounceY = (float) Math.abs(Math.sin(t * (crouch ? 6.0F : 3.0F) + ph)) * (crouch ? 0.12F : 0.05F);

        ps.translate(0.0, 0.9, 0.0);
        ps.scale(scaleX, scaleY, scaleZ);
        ps.translate(0.0, -0.9, 0.0);

        ps.mulPose(Axis.XP.rotation(tiltX));
        ps.mulPose(Axis.ZP.rotation(tiltZ));
        ps.translate(0.0, bounceY, 0.0);

        if (crouch) {
            
            float spin = (float) Math.sin(t * 1.5F + ph) * 0.25F;
            ps.mulPose(Axis.YP.rotation(spin));
        }

        applyPlayerModelParts(event.getRenderer().getModel(), t, ph, crouch, moving);
    }

    @SubscribeEvent
    public static void onPlayerRenderPost(RenderPlayerEvent.Post event) {
        
        restorePlayerModel(event.getRenderer().getModel());
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        WiggleManager.clientEnabled = false;
        
        parts.clear();
        origRots.clear();
    }

    private static final List<ModelPart> parts    = new ArrayList<>();
    private static final List<float[]>   origRots = new ArrayList<>();

    private static void save(ModelPart part) {
        parts.add(part);
        origRots.add(new float[]{part.xRot, part.yRot, part.zRot});
    }

    private static void restoreAll() {
        for (int i = 0; i < parts.size(); i++) {
            float[] r = origRots.get(i);
            parts.get(i).xRot = r[0];
            parts.get(i).yRot = r[1];
            parts.get(i).zRot = r[2];
        }
        parts.clear();
        origRots.clear();
    }

    private static float[] pSX = new float[8];
    private static float[] pSY = new float[8];
    private static float[] pSZ = new float[8];

    private static void savePlayer(PlayerModel<?> model) {
        pSX[0] = model.head.xRot;     pSY[0] = model.head.yRot;     pSZ[0] = model.head.zRot;
        pSX[1] = model.hat.xRot;      pSY[1] = model.hat.yRot;      pSZ[1] = model.hat.zRot;
        pSX[2] = model.body.xRot;     pSY[2] = model.body.yRot;     pSZ[2] = model.body.zRot;
        pSX[3] = model.leftArm.xRot;  pSY[3] = model.leftArm.yRot;  pSZ[3] = model.leftArm.zRot;
        pSX[4] = model.rightArm.xRot; pSY[4] = model.rightArm.yRot; pSZ[4] = model.rightArm.zRot;
        pSX[5] = model.leftLeg.xRot;  pSY[5] = model.leftLeg.yRot;  pSZ[5] = model.leftLeg.zRot;
        pSX[6] = model.rightLeg.xRot; pSY[6] = model.rightLeg.yRot; pSZ[6] = model.rightLeg.zRot;
    }

    private static void restorePlayerModel(PlayerModel<?> model) {
        model.head.xRot     = pSX[0]; model.head.yRot     = pSY[0]; model.head.zRot     = pSZ[0];
        model.hat.xRot      = pSX[1]; model.hat.yRot      = pSY[1]; model.hat.zRot      = pSZ[1];
        model.body.xRot     = pSX[2]; model.body.yRot     = pSY[2]; model.body.zRot     = pSZ[2];
        model.leftArm.xRot  = pSX[3]; model.leftArm.yRot  = pSY[3]; model.leftArm.zRot  = pSZ[3];
        model.rightArm.xRot = pSX[4]; model.rightArm.yRot = pSY[4]; model.rightArm.zRot = pSZ[4];
        model.leftLeg.xRot  = pSX[5]; model.leftLeg.yRot  = pSY[5]; model.leftLeg.zRot  = pSZ[5];
        model.rightLeg.xRot = pSX[6]; model.rightLeg.yRot = pSY[6]; model.rightLeg.zRot = pSZ[6];
    }

    public static void applyPlayerModelParts(PlayerModel<?> model, float t, float ph,
                                              boolean crouching, boolean moving) {
        savePlayer(model);

        model.head.yRot += (float) Math.sin(t * 2.5F + ph) * 0.70F;
        model.head.xRot += (float) Math.cos(t * 1.8F + ph) * 0.45F;
        model.head.zRot += (float) Math.sin(t * 1.2F + ph) * 0.35F;
        model.hat.xRot   = model.head.xRot;
        model.hat.yRot   = model.head.yRot;
        model.hat.zRot   = model.head.zRot;

        if (crouching) {
            
            model.body.xRot += (float) Math.sin(t * 4.0F + ph) * 0.55F;
            model.body.zRot += (float) Math.cos(t * 5.0F + ph) * 0.70F;
            model.body.yRot += (float) Math.sin(t * 3.0F + ph) * 0.40F;

            model.leftArm.xRot  += (float) Math.sin(t * 6.0F)              * 1.20F;
            model.leftArm.yRot  += (float) Math.cos(t * 7.0F)              * 1.40F;
            model.leftArm.zRot  += (float) Math.sin(t * 5.0F + 1.2F)      * 1.30F;
            model.rightArm.xRot += (float) Math.sin(t * 6.0F + 1.8F)      * 1.20F;
            model.rightArm.yRot += (float) Math.cos(t * 7.0F + 1.5F)      * 1.40F;
            model.rightArm.zRot += (float) Math.sin(t * 5.0F + 3.0F)      * 1.30F;

            model.leftLeg.xRot  += (float) Math.sin(t * 5.5F + ph)         * 0.65F;
            model.leftLeg.zRot  += (float) Math.cos(t * 4.5F + ph)         * 0.55F;
            model.rightLeg.xRot += (float) Math.sin(t * 5.5F + ph + 1.5F)  * 0.65F;
            model.rightLeg.zRot += (float) Math.cos(t * 4.5F + ph + 1.0F)  * 0.55F;

        } else if (moving) {
            
            model.body.xRot += (float) Math.sin(t * 0.7F + ph) * 0.20F;
            model.body.zRot += (float) Math.cos(t * 0.8F + ph) * 0.25F;

            model.leftArm.xRot  += (float) Math.sin(t * 2.5F + ph)        * 1.10F;
            model.leftArm.yRot  += (float) Math.cos(t * 2.0F + ph)        * 1.50F;
            model.leftArm.zRot  += (float) Math.sin(t * 1.5F + ph)        * 0.80F;
            model.rightArm.xRot += (float) Math.cos(t * 2.5F + ph + 1.0F) * 1.10F;
            model.rightArm.yRot += (float) Math.sin(t * 2.0F + ph + 2.0F) * 1.50F;
            model.rightArm.zRot += (float) Math.cos(t * 1.5F + ph + 1.5F) * 0.80F;

            model.leftLeg.xRot  += (float) Math.sin(t * 1.8F + ph + 2.0F) * 0.50F;
            model.leftLeg.zRot  += (float) Math.cos(t * 1.5F + ph)         * 0.30F;
            model.rightLeg.xRot += (float) Math.sin(t * 1.8F + ph)         * 0.50F;
            model.rightLeg.zRot += (float) Math.cos(t * 1.5F + ph + 1.5F)  * 0.30F;

        } else {
            
            model.body.xRot += (float) Math.sin(t * 0.7F + ph) * 0.12F;
            model.body.zRot += (float) Math.cos(t * 0.8F + ph) * 0.14F;

            model.leftArm.xRot  += (float) Math.sin(t * 1.8F + ph)        * 0.45F;
            model.leftArm.zRot  += (float) Math.cos(t * 1.4F + ph)        * 0.40F;
            model.rightArm.xRot += (float) Math.sin(t * 1.8F + ph + 1.0F) * 0.45F;
            model.rightArm.zRot += (float) Math.cos(t * 1.4F + ph + 2.0F) * 0.40F;

            model.leftLeg.xRot  += (float) Math.sin(t * 1.5F + ph + 2.0F) * 0.25F;
            model.rightLeg.xRot += (float) Math.sin(t * 1.5F + ph)         * 0.25F;
        }
    }

    private static void applyMobWiggle(EntityModel<?> model, float t, long id, boolean isVillager) {
        parts.clear();
        origRots.clear();
        float ph = id * 1.618F;

        if (model instanceof HumanoidModel<?> hm) {
            wigglePart(hm.head,     t, ph, 0, isVillager ? 1.5F : 0.7F, isVillager);
            wigglePart(hm.body,     t, ph, 1, isVillager ? 0.5F : 0.2F, false);
            wigglePart(hm.leftArm,  t, ph, 2, 0.6F, false);
            wigglePart(hm.rightArm, t, ph, 3, 0.65F, false);
            wigglePart(hm.leftLeg,  t, ph, 4, 0.35F, false);
            wigglePart(hm.rightLeg, t, ph, 5, 0.35F, false);
        } else if (model instanceof HierarchicalModel<?> hModel) {
            wiggleNamed(hModel, t, ph, isVillager);
        }
    }

    private static void wigglePart(ModelPart part, float t, float ph, int idx, float strength, boolean extraCrazy) {
        save(part);
        float f1 = t * (1.3F + idx * 0.2F) + ph + idx;
        float f2 = t * (0.9F + idx * 0.15F) + ph * 1.4F + idx;
        float f3 = t * (0.7F + idx * 0.1F) + ph * 0.8F + idx;

        if (extraCrazy) {
            part.yRot = (t * 4.0F + ph) % (float) (Math.PI * 2);
            part.xRot += (float) Math.sin(f1 * 3.0F) * 0.6F;
            part.zRot += (float) Math.cos(f3 * 2.5F) * 0.5F;
        } else {
            part.xRot += (float) Math.sin(f1) * strength;
            part.yRot += (float) Math.cos(f2) * strength * 0.6F;
            part.zRot += (float) Math.sin(f3) * strength * 0.5F;
        }
    }

    private static final String[] NAMED = {
        "head", "body", "left_arm", "right_arm", "left_leg", "right_leg",
        "left_hind_leg", "right_hind_leg", "left_front_leg", "right_front_leg",
        "left_wing", "right_wing", "neck", "beak", "red_thing", "hat", "ears"
    };

    private static void wiggleNamed(HierarchicalModel<?> model, float t, float ph, boolean isVillager) {
        int idx = 0;
        for (String name : NAMED) {
            try {
                ModelPart p = model.root().getChild(name);
                wigglePart(p, t, ph, idx, 0.5F, isVillager && name.equals("head"));
                idx++;
            } catch (Exception ignored) {}
        }
    }
}