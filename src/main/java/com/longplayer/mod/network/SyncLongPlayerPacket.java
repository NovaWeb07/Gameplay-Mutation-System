package com.longplayer.mod.network;

import com.longplayer.mod.data.LongPlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncLongPlayerPacket {
    private final UUID playerUUID;
    private final int segmentCount;
    private final boolean snakeMode;

    public SyncLongPlayerPacket(UUID playerUUID, int segmentCount, boolean snakeMode) {
        this.playerUUID = playerUUID;
        this.segmentCount = segmentCount;
        this.snakeMode = snakeMode;
    }

    public static void encode(SyncLongPlayerPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeInt(msg.segmentCount);
        buf.writeBoolean(msg.snakeMode);
    }

    public static SyncLongPlayerPacket decode(FriendlyByteBuf buf) {
        return new SyncLongPlayerPacket(buf.readUUID(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(SyncLongPlayerPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LongPlayerData.setClientData(msg.playerUUID, msg.segmentCount, msg.snakeMode);
        });
        ctx.get().setPacketHandled(true);
    }
}