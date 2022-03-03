package com.klemenz.Utility;

public class Vec3 {
    private final Double x;
    private final Double y;
    private final Double z;

    public Vec3() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public Vec3(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public static Vec3 negate(Vec3 v) {
        return new Vec3(-v.getX(), -v.getY(), -v.getZ());
    }

    public static double length(Vec3 v) {
        return Math.sqrt(squaredLength(v));
    }

    public static double squaredLength(Vec3 v) {
        return Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2) + Math.pow(v.getZ(), 2);
    }

    public static Vec3 unitVector(Vec3 v) {
        return scale(v, 1 / length(v));
    }

    public static Vec3 scale(Vec3 v, double scalar) {
        return new Vec3(v.getX() * scalar, v.getY() * scalar, v.getZ() * scalar);
    }

    public static Vec3 add(Vec3 u, Vec3 v) {
        return new Vec3(u.getX() + v.getX(), u.getY() + v.getY(), u.getZ() + v.getZ());
    }

    public static Vec3 subtract(Vec3 u, Vec3 v) {
        return new Vec3(u.getX() - v.getX(), u.getY() - v.getY(), u.getZ() - v.getZ());
    }

    public static Vec3 multiply(Vec3 u, Vec3 v) {
        return new Vec3(u.getX() * v.getX(), u.getY() * v.getY(), u.getZ() * v.getZ());
    }

    public static double dot(Vec3 u, Vec3 v) {
        return u.getX() * v.getX()
             + u.getY() * v.getY()
             + u.getZ() * v.getZ();
    }

    public static Vec3 cross(Vec3 u, Vec3 v) {
        return new Vec3(u.getY() * v.getZ() - u.getZ() * v.getY(),
                        u.getZ() * v.getX() - u.getX() * v.getZ(),
                        u.getX() * v.getY() - u.getY() * v.getX());
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", x, y, z);
    }
}
