package com.klemenz.Utility;

public class Ray {
    private final Vec3 origin;
    private final Vec3 direction;

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vec3 at(double t) {
        return Vec3.add(origin, Vec3.scale(direction, t));
    }

    public Vec3 getOrigin() {
        return origin;
    }

    public Vec3 getDirection() {
        return direction;
    }
}
