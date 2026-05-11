package com.longplayer.mod;

import com.longplayer.mod.enderman.ModEntities;
import com.longplayer.mod.network.NetworkHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LongPlayerMod.MOD_ID)
public class LongPlayerMod {
    public static final String MOD_ID = "longplayer";
    public static final Logger LOGGER = LogManager.getLogger();

    public LongPlayerMod() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        ModEntities.register(modBus);
        com.longplayer.mod.enderman.ModItems.register(modBus);
        com.longplayer.mod.villager.ModVillagerEntities.register(modBus);
        com.longplayer.mod.meat.ModMeatEntities.register(modBus);
        LOGGER.info("Long Player Mod initialized!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.register();
            com.longplayer.mod.player.LongNeckNetwork.register();
        });
    }
}