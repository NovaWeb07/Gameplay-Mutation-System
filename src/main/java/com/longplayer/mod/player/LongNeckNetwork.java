package com.longplayer.mod.player;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class LongNeckNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation("longplayer", "longneck_channel"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, SyncNeckPacket.class, SyncNeckPacket::encode, SyncNeckPacket::new, SyncNeckPacket::handle);
    }

    public static class SyncNeckPacket {
        private final int entityId;
        private final int length;

        public SyncNeckPacket(int entityId, int length) {
            this.entityId = entityId;
            this.length = length;
        }

        public SyncNeckPacket(net.minecraft.network.FriendlyByteBuf buf) {
            this.entityId = buf.readInt();
            this.length = buf.readInt();
        }

        public void encode(net.minecraft.network.FriendlyByteBuf buf) {
            buf.writeInt(entityId);
            buf.writeInt(length);
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                
                if (Minecraft.getInstance().level != null) {
                    net.minecraft.world.entity.Entity entity = Minecraft.getInstance().level.getEntity(entityId);
                    if (entity instanceof net.minecraft.world.entity.player.Player player) {
                        player.getPersistentData().putInt("LongNeckLength", length);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}