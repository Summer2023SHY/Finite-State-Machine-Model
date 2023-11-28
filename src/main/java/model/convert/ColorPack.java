package model.convert;

import org.apache.commons.lang3.IntegerRange;

public class ColorPack {

    private static final double COLOR_ADJUST = .3;

    private static final IntegerRange COLOR_RANGE = IntegerRange.of(0, 255);

    private int r;
    private int g;
    private int b;

    public ColorPack(int r, int g, int b) {
        this.r = COLOR_RANGE.fit(r);
        this.g = COLOR_RANGE.fit(g);
        this.b = COLOR_RANGE.fit(b);
    }

    public ColorPack(String r, String g, String b) {
        this(Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16));
    }

    public String getGraphvizColor() {
        String out = "#" + hex(r) + hex(g) + hex(b);
        return out;
    }

    private String hex(int in) {
        String out = Integer.toHexString(in);
        if(out.length() == 1) {
            out = "0" + out;
        }
        return out;
    }

    public ColorPack cycleColor(int in) {
        ColorPack start = new ColorPack(r, g, b);
        for(int i = 1; i < in; i++) {
            start = start.cycleColor();
        }
        return start;
    }

    public ColorPack cycleColor() {
        return new ColorPack(r + (int)((255 - r) * COLOR_ADJUST), g + (int)((255 - g) * COLOR_ADJUST), b + (int)((255 - b) * COLOR_ADJUST));
    }

    @Override
    public String toString() {
        return getGraphvizColor();
    }

}
