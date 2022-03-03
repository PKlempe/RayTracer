package com.klemenz.Utility;

public class HitPoint {
    // This struct-like class has been inspired by the book "Ray Tracing in One Weekend"
    // https://raytracing.github.io/books/RayTracingInOneWeekend.html#metal/adatastructuretodescriberay-objectintersections
    // #################################################################################################################
    public Vec3 position;
    public Vec3 normal;
    public Color color;
    public boolean isOnFrontFace;
    public double[] phongValues;
    public double t;
    public double reflectance;
    public double transmittance;
    public double refractionIOF;

    public void setFaceNormal(Ray ray, Vec3 outwardNormal) {
        isOnFrontFace = Vec3.dot(ray.getDirection(), outwardNormal) < 0;
        normal = isOnFrontFace ? outwardNormal : Vec3.negate(outwardNormal);
    }
    // #################################################################################################################
}
