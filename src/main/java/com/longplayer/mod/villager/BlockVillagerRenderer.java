package com.longplayer.mod.villager;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlockVillagerRenderer extends MobRenderer<BlockVillagerEntity, VillagerModel<BlockVillagerEntity>> {
    private static final ResourceLocation VILLAGER_TEXTURE =
        new ResourceLocation("textures/entity/villager/villager.png");

    public BlockVillagerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new VillagerModel<>(ctx.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BlockVillagerEntity entity) {
        return VILLAGER_TEXTURE;
    }
}