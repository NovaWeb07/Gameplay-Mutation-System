package com.longplayer.mod.network;

import com.longplayer.mod.villager.VillagerNoseManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncVillagerNosePacket {

    private final boolean active;
    private final long startTime;

    public SyncVillagerNosePacket(boolean active, long startTime) {
        this.active = active;
        this.startTime = startTime;
    }

    public SyncVillagerNosePacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean();
        this.startTime = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(active);
        buf.writeLong(startTime);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            VillagerNoseManager.setClientNose(this.active, this.startTime);
        });
        ctx.get().setPacketHandled(true);
    }
}