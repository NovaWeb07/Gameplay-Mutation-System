package com.longplayer.mod.enderman;

import com.longplayer.mod.LongPlayerMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FatEndermanModel<T extends FatEndermanEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(new ResourceLocation(LongPlayerMod.MOD_ID, "fat_enderman"), "main");

    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart body;
    private final ModelPart belly;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    public boolean carrying;

    public FatEndermanModel(ModelPart root) {
        this.head = root.getChild("head");
        this.hat = root.getChild("hat");
        this.body = root.getChild("body");
        this.belly = root.getChild("belly");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
            PartPose.offset(0.0F, 14.0F, 0.0F));

        root.addOrReplaceChild("hat", CubeListBuilder.create()
            .texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                new CubeDeformation(0.5F)),
            PartPose.offset(0.0F, 14.0F, 0.0F));

        root.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(32, 16).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 6.0F, 6.0F),
            PartPose.offset(0.0F, 14.0F, 0.0F));

        root.addOrReplaceChild("belly", CubeListBuilder.create()
            .texOffs(32, 16).addBox(-6.0F, 1.0F, -6.0F, 12.0F, 5.0F, 3.0F),
            PartPose.offset(0.0F, 14.0F, 0.0F));

        root.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(56, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F),
            PartPose.offset(-6.0F, 15.0F, 0.0F));

        root.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(56, 0).mirror().addBox(-1.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F),
            PartPose.offset(6.0F, 15.0F, 0.0F));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create()
            .texOffs(56, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
            PartPose.offset(-3.0F, 20.0F, 0.0F));

        root.addOrReplaceChild("left_leg", CubeListBuilder.create()
            .texOffs(56, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
            PartPose.offset(3.0F, 20.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch) {
        
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.hat.yRot = this.head.yRot;
        this.hat.xRot = this.head.xRot;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.5F * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.5F * limbSwingAmount;

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.2F * limbSwingAmount;

        this.belly.zRot = Mth.sin(ageInTicks * 0.05F) * 0.02F;

        if (this.carrying) {
            this.rightArm.xRot = -0.5F;
            this.leftArm.xRot = -0.5F;
            this.rightArm.zRot = 0.05F;
            this.leftArm.zRot = -0.05F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                                int packedOverlay, float r, float g, float b, float a) {
        head.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        hat.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        body.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        belly.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        leftArm.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }
}