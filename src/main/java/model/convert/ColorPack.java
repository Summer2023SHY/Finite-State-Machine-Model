package model.convert;

public class ColorPack {

    private static final double COLOR_ADJUST = .3;

    private int r;
    private int g;
    private int b;

    public ColorPack(int r, int g, int b) {
        this.r = affixColor(r);
        this.g = affixColor(g);
        this.b = affixColor(b);
    }

    public ColorPack(String r, String g, String b) {
        this.r = affixColor(Integer.parseInt(r, 16));
        this.g = affixColor(Integer.parseInt(g, 16));
        this.b = affixColor(Integer.parseInt(b, 16));
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

    private int affixColor(int in) {
        return in < 0 ? 0 : in > 255 ? 255 : in;
    }

    @Override
    public String toString() {
        return getGraphvizColor();
    }

}
