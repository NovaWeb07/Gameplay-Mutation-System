package com.longplayer.mod.golem;

import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HelicopterGolemModel extends IronGolemModel<HelicopterGolemEntity> {

    public HelicopterGolemModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(HelicopterGolemEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        
        int startupTicks = entity.getStartupTicks();
        float fraction = Mth.clamp(startupTicks / 100.0F, 0.0F, 1.0F);
        
        ModelPart rightArm = this.root().getChild("right_arm");
        ModelPart leftArm = this.root().getChild("left_arm");
        ModelPart rightLeg = this.root().getChild("right_leg");
        ModelPart leftLeg = this.root().getChild("left_leg");
        ModelPart body = this.root().getChild("body");

        float targetXRot = -(float) Math.PI / 2.0F; 
        rightArm.xRot = Mth.lerp(fraction, rightArm.xRot, targetXRot);
        leftArm.xRot = Mth.lerp(fraction, leftArm.xRot, targetXRot);
        
        rightArm.x = Mth.lerp(fraction, 0.0F, 11.0F);
        leftArm.x = Mth.lerp(fraction, 0.0F, -11.0F);
        
        rightArm.zRot = Mth.lerp(fraction, rightArm.zRot, 0.0F);
        leftArm.zRot = Mth.lerp(fraction, leftArm.zRot, 0.0F);
        
        if (startupTicks >= 100) {
            float spinSpeed = 2.0F;
            float yRotate = ageInTicks * spinSpeed;
            
            rightArm.yRot = yRotate;
            leftArm.yRot = yRotate + (float) Math.PI;
            
            rightLeg.xRot = 0;
            leftLeg.xRot = 0;
            
            body.xRot = (float) Math.toRadians(15);
        } else {
            rightArm.yRot = 0;
            leftArm.yRot = 0;
            body.xRot = 0;
        }
    }
}