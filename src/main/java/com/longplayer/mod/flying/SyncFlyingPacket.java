package com.longplayer.mod.flying;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncFlyingPacket {
    private final boolean enabled;

    public SyncFlyingPacket(boolean enabled) {
        this.enabled = enabled;
    }

    public SyncFlyingPacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> FlyingManager.clientEnabled = enabled);
        ctx.get().setPacketHandled(true);
    }
}