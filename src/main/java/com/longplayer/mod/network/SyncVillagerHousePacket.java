package com.longplayer.mod.network;

import com.longplayer.mod.villager.VillagerHouseManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncVillagerHousePacket {
    public final boolean isEnabled;

    public SyncVillagerHousePacket(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public SyncVillagerHousePacket(FriendlyByteBuf buf) {
        this.isEnabled = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isEnabled);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            VillagerHouseManager.isClientEnabled = this.isEnabled;
        });
        ctx.get().setPacketHandled(true);
    }
}