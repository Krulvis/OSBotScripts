package api.util;

import java.awt.*;

/**
 * Created by Krulvis on 12-Mar-17.
 */
public class DynamicColor {

    private long lastColorUpdate = 0;
    private int interval = 100;
    private float min = 0.40f, max = 0.75f, step = 0.01f;
    private float bgHue = min;
    private boolean hueUp = true;
    private Color custom = null;

    private DynamicColor() {

    }

    public DynamicColor(float min, float max) {
        this(min, max, 0.01f, 100);
    }

    public DynamicColor(float min, float max, float step) {
        this(min, max, step, 100);
    }

    public DynamicColor(float min, float max, float step, int interval) {
        this.min = min;
        this.max = max;
        this.step = step;
        this.interval = interval;
        bgHue = min;
        hueUp = true;
        updateColor();
    }

    public Color get(boolean update) {
        if (update) {
            updateColor();
        }
        return custom;
    }

    public Color get() {
        return get(true); //update by default
    }

    public void updateColor() {
        if (lastColorUpdate + interval > System.currentTimeMillis()) {
            return;
        }
        lastColorUpdate = System.currentTimeMillis();
        custom = new Color(HSBtoRGB(bgHue, 1.0f, 1.0f, 0xff), true);
        bgHue += hueUp ? step : -step;
        if ((hueUp && bgHue >= max) || (!hueUp && bgHue <= min)) {
            hueUp = !hueUp;
        }
    }

    private int HSBtoRGB(float hue, float saturation, float brightness, int alpha) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return (alpha << 24) | (r << 16) | (g << 8) | (b);
    }
}
