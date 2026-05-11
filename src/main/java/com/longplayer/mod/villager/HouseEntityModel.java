package com.longplayer.mod.villager;

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
import net.minecraft.world.entity.npc.Villager;

public class HouseEntityModel extends EntityModel<Villager> {
    public static final ModelLayerLocation LAYER_LOCATION =
        new ModelLayerLocation(new ResourceLocation(LongPlayerMod.MOD_ID, "house_model"), "main");

    public final ModelPart base;
    public final ModelPart roof;
    public final ModelPart leftLeg;
    public final ModelPart rightLeg;

    public HouseEntityModel(ModelPart root) {
        this.base = root.getChild("base");
        this.roof = root.getChild("roof");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("base", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-8.0F, 8.0F, -8.0F, 16.0F, 12.0F, 16.0F), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition roofPart = root.addOrReplaceChild("roof", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        
        roofPart.addOrReplaceChild("roof1", CubeListBuilder.create().texOffs(0, 0)
            .addBox(-9.0F, 4.0F, -9.0F, 18.0F, 4.0F, 18.0F), PartPose.ZERO);
        
        roofPart.addOrReplaceChild("roof2", CubeListBuilder.create().texOffs(0, 0)
            .addBox(-7.0F, 0.0F, -7.0F, 14.0F, 4.0F, 14.0F), PartPose.ZERO);
            
        roofPart.addOrReplaceChild("roof3", CubeListBuilder.create().texOffs(0, 0)
            .addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 10.0F), PartPose.ZERO);

        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(4.0F, 20.0F, 0.0F));
            
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(-4.0F, 20.0F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(Villager entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;

        float wobble = Mth.abs(Mth.sin(limbSwing * 0.6662F)) * limbSwingAmount * 2.0F;
        this.base.y = wobble;
        this.roof.y = wobble;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float r, float g, float b, float a) {
        
    }
}