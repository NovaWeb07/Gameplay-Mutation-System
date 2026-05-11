package com.longplayer.mod.network;

import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.network.SyncVillagerHousePacket;
import com.longplayer.mod.wiggle.SyncWigglePacket;
import com.longplayer.mod.flying.SyncFlyingPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(LongPlayerMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(SyncLongPlayerPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncLongPlayerPacket::decode)
            .encoder(SyncLongPlayerPacket::encode)
            .consumerMainThread(SyncLongPlayerPacket::handle)
            .add();

        CHANNEL.messageBuilder(SyncVillagerHousePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncVillagerHousePacket::new)
            .encoder(SyncVillagerHousePacket::toBytes)
            .consumerMainThread(SyncVillagerHousePacket::handle)
            .add();

        CHANNEL.messageBuilder(SyncVillagerNosePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncVillagerNosePacket::new)
            .encoder(SyncVillagerNosePacket::toBytes)
            .consumerMainThread(SyncVillagerNosePacket::handle)
            .add();
        CHANNEL.messageBuilder(SyncWigglePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncWigglePacket::new)
            .encoder(SyncWigglePacket::toBytes)
            .consumerMainThread(SyncWigglePacket::handle)
            .add();
        CHANNEL.messageBuilder(SyncFlyingPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .decoder(SyncFlyingPacket::new)
            .encoder(SyncFlyingPacket::toBytes)
            .consumerMainThread(SyncFlyingPacket::handle)
            .add();
    }

    public static void sendToAllClients(Object message) {
        CHANNEL.send(net.minecraftforge.network.PacketDistributor.ALL.noArg(), message);
    }
}