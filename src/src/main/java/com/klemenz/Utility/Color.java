package com.klemenz.Utility;

public class Color {
    private final double r;
    private final double g;
    private final double b;

    public Color(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(int rgb) {
        this.r = ((rgb>>16)&0xFF) / 255.0;
        this.g = ((rgb>>8)&0xFF) / 255.0;
        this.b = ((rgb)&0xFF) / 255.0;
    }

    // Inspired by Javas implementation of Color.getRGB()
    // https://docs.oracle.com/javase/7/docs/api/java/awt/Color.html#getRGB()
    // #################################################################################################################
    public int getRGB() {
        int transR = (int) (255.999 * r);
        int transG = (int) (255.999 * g);
        int transB = (int) (255.999 * b);

        return ((0xFF) << 24) |
               ((transR & 0xFF) << 16) |
               ((transG & 0xFF) << 8)  |
               ((transB & 0xFF));
    }
    // #################################################################################################################

    public double getR() {
        return r;
    }

    public double getG() {
        return g;
    }

    public double getB() {
        return b;
    }

    public static Color add(Color a, Color b) {
        return new Color(
                Math.min(a.getR() + b.getR(), 1.0),
                Math.min(a.getG() + b.getG(), 1.0),
                Math.min(a.getB() + b.getB(), 1.0)
        );
    }

    public static Color multiply(Color color, double value) {
        return new Color(
                color.getR() * value,
                color.getG() * value,
                color.getB() * value
        );
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", r, g, b);
    }
}
