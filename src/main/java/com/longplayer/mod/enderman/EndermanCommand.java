package com.longplayer.mod.enderman;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;

public class EndermanCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("enderman")
            .then(Commands.argument("count", IntegerArgumentType.integer(1, 500))
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    int count = IntegerArgumentType.getInteger(ctx, "count");
                    return spawnRidingEndermen(player, count);
                })
            )
        );
    }

    private static int spawnRidingEndermen(ServerPlayer player, int count) {
        ServerLevel level = player.serverLevel();
        int spawned = 0;

        for (int i = 0; i < count; i++) {
            
            double x = player.getX() + (level.random.nextDouble() - 0.5) * 30;
            double y = player.getY() + 3 + level.random.nextDouble() * 15;
            double z = player.getZ() + (level.random.nextDouble() - 0.5) * 30;

            Chicken chicken = EntityType.CHICKEN.create(level);
            if (chicken == null) continue;
            chicken.moveTo(x, y, z, level.random.nextFloat() * 360, 0);
            level.addFreshEntity(chicken);

            FatEndermanEntity enderman = ModEntities.FAT_ENDERMAN.get().create(level);
            if (enderman == null) continue;
            enderman.moveTo(x, y, z, level.random.nextFloat() * 360, 0);
            level.addFreshEntity(enderman);

            enderman.startRiding(chicken, true);
            spawned++;
        }

        player.sendSystemMessage(Component.literal(
            "§aSpawned §e" + spawned + "§a flying Fat Endermen on Chickens! §6🐔"
        ));
        return spawned;
    }
}