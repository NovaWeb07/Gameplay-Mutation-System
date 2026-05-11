package com.longplayer.mod.wiggle;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncWigglePacket {
    private final boolean enabled;

    public SyncWigglePacket(boolean enabled) {
        this.enabled = enabled;
    }

    public SyncWigglePacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WiggleManager.clientEnabled = this.enabled;
        });
        ctx.get().setPacketHandled(true);
    }
}