package com.longplayer.mod.villager;

import com.longplayer.mod.enderman.ModEntities;
import com.longplayer.mod.network.NetworkHandler;
import com.longplayer.mod.network.SyncVillagerHousePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VillagerHouseManager {
    public static boolean isEnabled = false;
    public static boolean isClientEnabled = false;

    private static final Map<BlockPos, BlockState> savedBlocks = new HashMap<>();
    private static final List<UUID> spawnedVillagers = new ArrayList<>();

    private static boolean isHouseBlock(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS)
            || state.is(BlockTags.STAIRS) || state.is(BlockTags.WOODEN_DOORS)
            || state.is(BlockTags.SLABS) || state.is(BlockTags.WALLS)
            || state.is(BlockTags.FENCES) || state.is(Blocks.COBBLESTONE)
            || state.is(Blocks.MOSSY_COBBLESTONE) || state.is(Blocks.SMOOTH_STONE)
            || state.is(Blocks.STONE_BRICKS) || state.is(Blocks.CRACKED_STONE_BRICKS)
            || state.is(Blocks.TERRACOTTA) || state.is(Blocks.WHITE_TERRACOTTA)
            || state.is(Blocks.YELLOW_TERRACOTTA) || state.is(Blocks.RED_TERRACOTTA)
            || state.is(Blocks.ORANGE_TERRACOTTA) || state.is(Blocks.SANDSTONE)
            || state.is(Blocks.SMOOTH_SANDSTONE) || state.is(Blocks.CUT_SANDSTONE)
            || state.is(Blocks.GLASS_PANE) || state.is(Blocks.GLASS)
            || state.is(Blocks.IRON_BARS) || state.is(Blocks.BOOKSHELF)
            || state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)
            || state.is(Blocks.LANTERN) || state.is(Blocks.CAMPFIRE);
    }

    public static void enable(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        isEnabled = true;
        BlockPos center = player.blockPosition();
        int r = 25; 
        int yRange = 25; 
        int replaced = 0;

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -yRange, -r), center.offset(r, yRange, r))) {
            BlockState state = level.getBlockState(pos);
            if (isHouseBlock(state)) {
                savedBlocks.put(pos.immutable(), state);

                BlockVillagerEntity villager = com.longplayer.mod.villager.ModVillagerEntities.BLOCK_VILLAGER.get().create(level);
                if (villager != null) {
                    villager.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360f, 0);
                    villager.yBodyRot = villager.getYRot();
                    villager.yHeadRot = villager.getYRot();
                    level.addFreshEntity(villager);
                    spawnedVillagers.add(villager.getUUID());
                }

                if (state.blocksMotion()) {
                    level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
                replaced++;
            }
        }
        player.sendSystemMessage(Component.literal("§a[Cursed Mode] §e" + replaced + " blocks near you became Villagers! §6🏠↔️🧔"));
        NetworkHandler.sendToAllClients(new SyncVillagerHousePacket(isEnabled));
    }

    public static void disable(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        isEnabled = false;
        
        for (UUID uuid : spawnedVillagers) {
            Entity e = level.getEntity(uuid);
            if (e != null) {
                e.discard();
            }
        }
        spawnedVillagers.clear();

        for (Map.Entry<BlockPos, BlockState> entry : savedBlocks.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), 3);
        }
        savedBlocks.clear();
        player.sendSystemMessage(Component.literal("§c[Cursed Mode OFF] §eVillages restored to normal."));
        
        NetworkHandler.sendToAllClients(new SyncVillagerHousePacket(isEnabled));
    }
}