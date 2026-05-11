package com.longplayer.mod.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LongPlayerData {
    private static final Map<UUID, LongPlayerData> CLIENT_DATA = new ConcurrentHashMap<>();
    private static final Map<UUID, LongPlayerData> SERVER_DATA = new ConcurrentHashMap<>();

    private static final int TICKS_PER_SEGMENT = 2; 

    private int segmentCount;
    private boolean snakeMode;
    private final Deque<SegmentPose> positionHistory = new ArrayDeque<>();

    public LongPlayerData(int segmentCount, boolean snakeMode) {
        this.segmentCount = segmentCount;
        this.snakeMode = snakeMode;
    }

    public int getSegmentCount() { return segmentCount; }
    public boolean isSnakeMode() { return snakeMode; }
    public void setSegmentCount(int count) { this.segmentCount = count; }
    public void setSnakeMode(boolean mode) { this.snakeMode = mode; }

    private static final double MIN_RECORD_DISTANCE = 0.15; 

    public void recordPosition(double x, double y, double z, float yRot, float xRot) {
        
        if (!positionHistory.isEmpty()) {
            SegmentPose last = positionHistory.peekFirst();
            double dx = x - last.x;
            double dy = y - last.y;
            double dz = z - last.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq < MIN_RECORD_DISTANCE * MIN_RECORD_DISTANCE) {
                
                last.yRot = yRot;
                last.xRot = xRot;
                return;
            }
        }
        positionHistory.addFirst(new SegmentPose(x, y, z, yRot, xRot));
        int maxSize = (segmentCount + 2) * TICKS_PER_SEGMENT + 10;
        while (positionHistory.size() > maxSize) {
            positionHistory.removeLast();
        }
    }

    private static SegmentPose lerpPose(SegmentPose a, SegmentPose b, double t) {
        return new SegmentPose(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t,
            (float)(a.yRot + angleDiff(a.yRot, b.yRot) * t),
            (float)(a.xRot + (b.xRot - a.xRot) * t)
        );
    }

    private static float angleDiff(float from, float to) {
        float diff = to - from;
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        return diff;
    }

    public List<SegmentPose> getSegmentPoses(double playerX, double playerY, double playerZ,
                                              float playerYRot, float playerXRot) {
        List<SegmentPose> poses = new ArrayList<>();

        if (snakeMode) {
            
            SegmentPose[] history = positionHistory.toArray(new SegmentPose[0]);
            for (int i = 0; i < segmentCount; i++) {
                float exactIdx = (i + 1) * TICKS_PER_SEGMENT;
                int idxA = (int) Math.floor(exactIdx);
                int idxB = idxA + 1;
                float frac = exactIdx - idxA;

                if (idxB < history.length) {
                    poses.add(lerpPose(history[idxA], history[idxB], frac));
                } else if (idxA < history.length) {
                    poses.add(history[idxA].copy());
                } else if (history.length > 0) {
                    poses.add(history[history.length - 1].copy());
                } else {
                    double rad = Math.toRadians(playerYRot);
                    poses.add(new SegmentPose(
                        playerX + Math.sin(rad) * (i + 1) * 0.35,
                        playerY,
                        playerZ - Math.cos(rad) * (i + 1) * 0.35,
                        playerYRot, 0
                    ));
                }
            }
            
            for (int i = 0; i < poses.size(); i++) {
                double refX, refZ;
                if (i == 0) { refX = playerX; refZ = playerZ; }
                else { refX = poses.get(i - 1).x; refZ = poses.get(i - 1).z; }
                SegmentPose p = poses.get(i);
                double dx = refX - p.x;
                double dz = refZ - p.z;
                if (dx * dx + dz * dz > 0.0001) {
                    p.yRot = (float) Math.toDegrees(Math.atan2(dx, dz));
                }
            }
        } else {
            
            double rad = Math.toRadians(playerYRot);
            double dirX = Math.sin(rad);
            double dirZ = -Math.cos(rad);
            for (int i = 0; i < segmentCount; i++) {
                double dist = (i + 1) * 0.35;
                poses.add(new SegmentPose(
                    playerX + dirX * dist, playerY, playerZ + dirZ * dist,
                    playerYRot, playerXRot
                ));
            }
        }
        return poses;
    }

    public static void setClientData(UUID uuid, int count, boolean snake) {
        if (count <= 0) {
            CLIENT_DATA.remove(uuid);
        } else {
            LongPlayerData data = CLIENT_DATA.computeIfAbsent(uuid, k -> new LongPlayerData(count, snake));
            data.setSegmentCount(count);
            data.setSnakeMode(snake);
        }
    }

    public static LongPlayerData getClientData(UUID uuid) { return CLIENT_DATA.get(uuid); }
    public static Map<UUID, LongPlayerData> getAllClientData() { return CLIENT_DATA; }
    public static void removeClientData(UUID uuid) { CLIENT_DATA.remove(uuid); }

    public static void setServerData(UUID uuid, int count, boolean snake) {
        if (count <= 0) {
            SERVER_DATA.remove(uuid);
        } else {
            LongPlayerData data = SERVER_DATA.computeIfAbsent(uuid, k -> new LongPlayerData(count, snake));
            data.setSegmentCount(count);
            data.setSnakeMode(snake);
        }
    }

    public static LongPlayerData getServerData(UUID uuid) { return SERVER_DATA.get(uuid); }
    public static Map<UUID, LongPlayerData> getAllServerData() { return SERVER_DATA; }
    public static void removeServerData(UUID uuid) { SERVER_DATA.remove(uuid); }
}