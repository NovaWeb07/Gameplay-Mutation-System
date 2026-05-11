package com.longplayer.mod.wiggle;

import com.longplayer.mod.LongPlayerMod;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WiggleClientSetup {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        
        addLayerToSkin(event, "default");
        
        addLayerToSkin(event, "slim");
    }

    @SuppressWarnings("unchecked")
    private static void addLayerToSkin(EntityRenderersEvent.AddLayers event, String skinName) {
        
        var renderer = event.getSkin(skinName);
        if (renderer instanceof PlayerRenderer playerRenderer) {
            playerRenderer.addLayer(
                new WigglePlayerLayer(
                    (net.minecraft.client.renderer.entity.RenderLayerParent<
                        AbstractClientPlayer,
                        PlayerModel<AbstractClientPlayer>>) playerRenderer
                )
            );
        }
    }
}