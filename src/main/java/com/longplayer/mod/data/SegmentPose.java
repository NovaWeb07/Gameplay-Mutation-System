package com.longplayer.mod.data;

public class SegmentPose {
    public double x, y, z;
    public float yRot, xRot;

    public SegmentPose(double x, double y, double z, float yRot, float xRot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public SegmentPose copy() {
        return new SegmentPose(x, y, z, yRot, xRot);
    }
}