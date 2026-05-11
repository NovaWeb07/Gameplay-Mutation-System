package com.longplayer.mod.villager;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class VillagerHouseCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("villager")
            .then(Commands.literal("house")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    VillagerHouseManager.enable(player);
                    return 1;
                })
                .then(Commands.literal("off")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        VillagerHouseManager.disable(player);
                        return 1;
                    })
                )
            )
            .then(Commands.literal("Noselong")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    VillagerNoseManager.enable(player);
                    return 1;
                })
                .then(Commands.literal("off")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        VillagerNoseManager.disable(player);
                        return 1;
                    })
                )
            )
        );
    }
}