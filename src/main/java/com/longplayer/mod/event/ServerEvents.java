package com.longplayer.mod.event;

import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.command.LongCommand;
import com.longplayer.mod.data.LongPlayerData;
import com.longplayer.mod.network.NetworkHandler;
import com.longplayer.mod.network.SyncLongPlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LongCommand.register(event.getDispatcher());
        com.longplayer.mod.villager.VillagerHouseCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer joinedPlayer) {
            
            for (Map.Entry<UUID, LongPlayerData> entry : LongPlayerData.getAllServerData().entrySet()) {
                SyncLongPlayerPacket packet = new SyncLongPlayerPacket(
                    entry.getKey(),
                    entry.getValue().getSegmentCount(),
                    entry.getValue().isSnakeMode()
                );
                NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> joinedPlayer), packet
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        LongPlayerData.removeServerData(uuid);
        
        SyncLongPlayerPacket packet = new SyncLongPlayerPacket(uuid, 0, false);
        NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }
}