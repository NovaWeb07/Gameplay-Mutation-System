package com.longplayer.mod.wiggle;

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
public class WiggleCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("part")
            .then(Commands.literal("9")
                .then(Commands.literal("on")
                    .executes(ctx -> {
                        WiggleManager.serverEnabled = true;
                        NetworkHandler.sendToAllClients(new SyncWigglePacket(true));
                        ctx.getSource().sendSuccess(
                            () -> Component.literal("§aJelly Mode §fON! §7All mobs will wiggle!"),
                            false
                        );
                        return 1;
                    })
                )
                .then(Commands.literal("off")
                    .executes(ctx -> {
                        WiggleManager.serverEnabled = false;
                        NetworkHandler.sendToAllClients(new SyncWigglePacket(false));
                        ctx.getSource().sendSuccess(
                            () -> Component.literal("§cJelly Mode §fOFF."),
                            false
                        );
                        return 1;
                    })
                )
            )
        );
    }
}