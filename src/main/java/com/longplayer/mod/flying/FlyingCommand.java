package com.longplayer.mod.flying;

import com.longplayer.mod.LongPlayerMod;
import com.longplayer.mod.network.NetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LongPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlyingCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("part")
            .then(Commands.literal("10")
                .then(Commands.literal("on")
                    .executes(ctx -> {
                        FlyingManager.serverEnabled = true;
                        NetworkHandler.sendToAllClients(new SyncFlyingPacket(true));
                        ctx.getSource().sendSuccess(
                            () -> Component.literal("§b✦ Flying Madness §fON! §7Mobs will jump, fly and dance!"),
                            false
                        );
                        return 1;
                    })
                )
                .then(Commands.literal("off")
                    .executes(ctx -> {
                        FlyingManager.serverEnabled = false;
                        NetworkHandler.sendToAllClients(new SyncFlyingPacket(false));
                        ctx.getSource().sendSuccess(
                            () -> Component.literal("§c Flying Madness §fOFF."),
                            false
                        );
                        return 1;
                    })
                )
            )
        );
    }
}