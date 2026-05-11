package com.longplayer.mod.flying;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlyingClientEvents {

    private static final String[] SPIDER_LEGS = {
        "right_middle_front_leg", "left_middle_front_leg",
        "right_middle_back_leg",  "left_middle_back_leg",
        "right_front_leg",        "left_front_leg",
        "right_back_leg",         "left_back_leg"
    };

    private static final List<ModelPart> savedParts = new ArrayList<>();
    private static final List<float[]> savedRots   = new ArrayList<>();

    @SubscribeEvent
    public static void onRenderPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!FlyingManager.clientEnabled) return;
        if (event.getEntity() instanceof Player) return;

        LivingEntity entity = event.getEntity();
        EntityModel<?> model = event.getRenderer().getModel();
        float t   = (entity.level().getGameTime() + event.getPartialTick()) * 0.12F;
        long  id  = entity.getId();
        float ph  = id * 1.618F;

        savedParts.clear();
        savedRots.clear();

        if (model instanceof HierarchicalModel<?> hm) {
            int legIdx = 0;
            for (String legName : SPIDER_LEGS) {
                try {
                    ModelPart leg = hm.root().getChild(legName);
                    float[] orig = {leg.xRot, leg.yRot, leg.zRot};
                    savedParts.add(leg);
                    savedRots.add(orig);

                    float lf = t * (2.5F + legIdx * 0.35F) + ph + legIdx * 0.9F;
                    leg.xRot += (float) Math.sin(lf) * 1.2F;
                    leg.yRot += (float) Math.cos(lf * 1.3F) * 1.0F;
                    leg.zRot += (float) Math.sin(lf * 0.7F + 1.0F) * 1.3F;
                    legIdx++;
                } catch (Exception ignored) {}
            }

            if (!entity.onGround()) {
                wiggleAirborne(hm, t, ph);
            }
        }

        if (!entity.onGround() && model instanceof HumanoidModel<?> hm) {
            saveAndWiggle(hm.leftArm,  t, ph, 0, 1.2F);
            saveAndWiggle(hm.rightArm, t, ph, 1, 1.2F);
            saveAndWiggle(hm.leftLeg,  t, ph, 2, 0.6F);
            saveAndWiggle(hm.rightLeg, t, ph, 3, 0.6F);
            saveAndWiggle(hm.head,     t, ph, 4, 0.5F);
            saveAndWiggle(hm.body,     t, ph, 5, 0.3F);
        }
    }

    @SubscribeEvent
    public static void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
        if (!FlyingManager.clientEnabled) return;
        if (event.getEntity() instanceof Player) return;

        for (int i = 0; i < savedParts.size(); i++) {
            float[] r = savedRots.get(i);
            savedParts.get(i).xRot = r[0];
            savedParts.get(i).yRot = r[1];
            savedParts.get(i).zRot = r[2];
        }
        savedParts.clear();
        savedRots.clear();
    }

    private static void wiggleAirborne(HierarchicalModel<?> model, float t, float ph) {
        String[] parts = {"head", "body", "left_arm", "right_arm", "left_leg", "right_leg"};
        int idx = 10;
        for (String name : parts) {
            try {
                ModelPart p = model.root().getChild(name);
                saveAndWiggle(p, t, ph, idx, 0.7F);
                idx++;
            } catch (Exception ignored) {}
        }
    }

    private static void saveAndWiggle(ModelPart part, float t, float ph, int idx, float strength) {
        savedParts.add(part);
        savedRots.add(new float[]{part.xRot, part.yRot, part.zRot});
        float f1 = t * (1.5F + idx * 0.18F) + ph + idx;
        float f2 = t * (1.1F + idx * 0.12F) + ph * 1.3F + idx;
        float f3 = t * (0.8F + idx * 0.1F)  + ph * 0.7F + idx;
        
        part.xRot += (float) Math.sin(f1) * strength * 1.4F;
        part.yRot += (float) Math.cos(f2) * strength;
        part.zRot += (float) Math.sin(f3) * strength * 1.1F;
    }
}