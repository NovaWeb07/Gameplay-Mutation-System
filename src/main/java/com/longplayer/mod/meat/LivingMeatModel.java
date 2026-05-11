package com.longplayer.mod.meat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LivingMeatModel extends EntityModel<LivingMeatEntity> {
    private final EntityModel<LivingMeatEntity> pigLegs;
    private final EntityModel<LivingMeatEntity> cowLegs;
    private final EntityModel<LivingMeatEntity> sheepLegs;
    private final EntityModel<LivingMeatEntity> chickenLegs;
    private EntityModel<LivingMeatEntity> activeModel;

    public LivingMeatModel(EntityRendererProvider.Context ctx) {
        ModelPart mainRoot = ctx.bakeLayer(LivingMeatRenderer.LAYER_LOCATION);
        this.pigLegs = new QuadrupedLegsModel(mainRoot.getChild("pig"));
        this.cowLegs = new QuadrupedLegsModel(mainRoot.getChild("cow"));
        this.sheepLegs = new QuadrupedLegsModel(mainRoot.getChild("sheep"));
        
        ModelPart chickenRoot = ctx.bakeLayer(LivingMeatRenderer.CHICKEN_LAYER);
        this.chickenLegs = new BipedLegsModel(chickenRoot.getChild("chicken"));
        
        this.activeModel = this.pigLegs;
    }

    public static LayerDefinition createQuadrupedLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition pig = root.addOrReplaceChild("pig", CubeListBuilder.create(), PartPose.ZERO);
        CubeListBuilder pigFoot = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F);
        pig.addOrReplaceChild("right_hind_leg", pigFoot, PartPose.offset(-3.0F, 18.0F, 5.0F));
        pig.addOrReplaceChild("left_hind_leg", pigFoot, PartPose.offset(3.0F, 18.0F, 5.0F));
        pig.addOrReplaceChild("right_front_leg", pigFoot, PartPose.offset(-3.0F, 18.0F, -5.0F));
        pig.addOrReplaceChild("left_front_leg", pigFoot, PartPose.offset(3.0F, 18.0F, -5.0F));

        PartDefinition cow = root.addOrReplaceChild("cow", CubeListBuilder.create(), PartPose.ZERO);
        CubeListBuilder cowFoot = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        cow.addOrReplaceChild("right_hind_leg", cowFoot, PartPose.offset(-4.0F, 12.0F, 6.0F));
        cow.addOrReplaceChild("left_hind_leg", cowFoot, PartPose.offset(4.0F, 12.0F, 6.0F));
        cow.addOrReplaceChild("right_front_leg", cowFoot, PartPose.offset(-4.0F, 12.0F, -5.0F));
        cow.addOrReplaceChild("left_front_leg", cowFoot, PartPose.offset(4.0F, 12.0F, -5.0F));

        PartDefinition sheep = root.addOrReplaceChild("sheep", CubeListBuilder.create(), PartPose.ZERO);
        CubeListBuilder sheepFoot = CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        sheep.addOrReplaceChild("right_hind_leg", sheepFoot, PartPose.offset(-3.0F, 12.0F, 6.0F));
        sheep.addOrReplaceChild("left_hind_leg", sheepFoot, PartPose.offset(3.0F, 12.0F, 6.0F));
        sheep.addOrReplaceChild("right_front_leg", sheepFoot, PartPose.offset(-3.0F, 12.0F, -5.0F));
        sheep.addOrReplaceChild("left_front_leg", sheepFoot, PartPose.offset(3.0F, 12.0F, -5.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition createChickenLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition chicken = root.addOrReplaceChild("chicken", CubeListBuilder.create(), PartPose.ZERO);
        CubeListBuilder chickenFoot = CubeListBuilder.create().texOffs(26, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F);
        chicken.addOrReplaceChild("right_leg", chickenFoot, PartPose.offset(-2.0F, 19.0F, 1.0F));
        chicken.addOrReplaceChild("left_leg", chickenFoot, PartPose.offset(2.0F, 19.0F, 1.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(LivingMeatEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack meat = entity.getMeatItem();
        if (meat.is(Items.BEEF) || meat.is(Items.COOKED_BEEF)) this.activeModel = this.cowLegs;
        else if (meat.is(Items.MUTTON) || meat.is(Items.COOKED_MUTTON)) this.activeModel = this.sheepLegs;
        else if (meat.is(Items.CHICKEN) || meat.is(Items.COOKED_CHICKEN)) this.activeModel = this.chickenLegs;
        else this.activeModel = this.pigLegs;

        this.activeModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.activeModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static class QuadrupedLegsModel extends EntityModel<LivingMeatEntity> {
        private final ModelPart rightHindLeg;
        private final ModelPart leftHindLeg;
        private final ModelPart rightFrontLeg;
        private final ModelPart leftFrontLeg;

        public QuadrupedLegsModel(ModelPart root) {
            this.rightHindLeg = root.getChild("right_hind_leg");
            this.leftHindLeg = root.getChild("left_hind_leg");
            this.rightFrontLeg = root.getChild("right_front_leg");
            this.leftFrontLeg = root.getChild("left_front_leg");
        }

        @Override
        public void setupAnim(LivingMeatEntity entity, float f, float f1, float f2, float f3, float f4) {
            this.rightHindLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * f1;
            this.leftHindLeg.xRot = Mth.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
            this.rightFrontLeg.xRot = Mth.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
            this.leftFrontLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * f1;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.translate(0, 1.5, 0);
            poseStack.scale(1.2F, 1.3F, 1.2F);
            poseStack.translate(0, -1.5, 0);

            this.rightHindLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            this.leftHindLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            this.rightFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            this.leftFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    public static class BipedLegsModel extends EntityModel<LivingMeatEntity> {
        private final ModelPart rightLeg;
        private final ModelPart leftLeg;

        public BipedLegsModel(ModelPart root) {
            this.rightLeg = root.getChild("right_leg");
            this.leftLeg = root.getChild("left_leg");
        }

        @Override
        public void setupAnim(LivingMeatEntity entity, float f, float f1, float f2, float f3, float f4) {
            this.rightLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * f1;
            this.leftLeg.xRot = Mth.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            poseStack.pushPose();
            poseStack.translate(0, 1.5, 0);
            poseStack.scale(1.4F, 1.4F, 1.4F);
            poseStack.translate(0, -1.5, 0);

            this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }
}