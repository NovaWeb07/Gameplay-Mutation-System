package com.longplayer.mod.command;

import com.longplayer.mod.data.LongPlayerData;
import com.longplayer.mod.network.NetworkHandler;
import com.longplayer.mod.network.SyncLongPlayerPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class LongCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("long")
            .then(Commands.literal("player")
                
                .then(Commands.argument("count", IntegerArgumentType.integer(0, 150))
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        int count = IntegerArgumentType.getInteger(ctx, "count");
                        return setLong(player, count);
                    })
                )
                
                .then(Commands.literal("snake")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        return toggleSnake(player);
                    })
                )
                
                .then(Commands.literal("off")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        return setLong(player, 0);
                    })
                )
            )
        );
    }

    private static int setLong(ServerPlayer player, int count) {
        if (count <= 0) {
            LongPlayerData.removeServerData(player.getUUID());
            player.sendSystemMessage(Component.literal("§eLong Player §cOFF"));
        } else {
            LongPlayerData existing = LongPlayerData.getServerData(player.getUUID());
            boolean snake = existing != null && existing.isSnakeMode();
            LongPlayerData.setServerData(player.getUUID(), count, snake);
            player.sendSystemMessage(Component.literal(
                "§eLong Player: §a" + count + " segments" + (snake ? " §6[SNAKE]" : "")
            ));
        }
        syncToClients(player);
        return 1;
    }

    private static int toggleSnake(ServerPlayer player) {
        LongPlayerData data = LongPlayerData.getServerData(player.getUUID());
        if (data == null) {
            
            LongPlayerData.setServerData(player.getUUID(), 10, true);
            player.sendSystemMessage(Component.literal("§eLong Player: §a10 segments §6[SNAKE ON]"));
        } else {
            boolean newSnake = !data.isSnakeMode();
            data.setSnakeMode(newSnake);
            player.sendSystemMessage(Component.literal(
                "§eSnake Mode: " + (newSnake ? "§aON" : "§cOFF")
            ));
        }
        syncToClients(player);
        return 1;
    }

    private static void syncToClients(ServerPlayer player) {
        LongPlayerData data = LongPlayerData.getServerData(player.getUUID());
        SyncLongPlayerPacket packet = new SyncLongPlayerPacket(
            player.getUUID(),
            data != null ? data.getSegmentCount() : 0,
            data != null && data.isSnakeMode()
        );
        NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }
}