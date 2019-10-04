package net.tropicraft.core.common;

public class ColorHelper {

    public static float getRed(int color) {
        return (float)(color >> 16 & 255) / 255.0F;
    }

    public static float getGreen(int color) {
        return (float)(color >> 8 & 255) / 255.0F;
    }

    public static float getBlue(int color) {
        return (float)(color & 255) / 255.0F;
    }

    /**
     *
     * @param red float value from 0-1 representing the red of this color
     * @param green float value from 0-1 representing the green of this color
     * @param blue float value from 0-1 representing the blue of this color
     * @return Returns a value of the combined rgb floats between 0 and 1 to a single int
     */
    public static int getColor(float red, float green, float blue) {
        return ((int)(red * 255) << 16) | ((int)(green * 255) << 8) | (int)(blue * 255);
    }

    public static int getColor(float[] rgb) {
        return getColor(rgb[0], rgb[1], rgb[2]);
    }
}
