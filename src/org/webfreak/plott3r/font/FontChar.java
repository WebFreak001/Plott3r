package org.webfreak.plott3r.font;

public class FontChar {
    private String path;
    private double offset;

    public FontChar(String path, double offset) {
        this.path = path;
        this.offset = offset;
    }

    public double getOffset() {
        return offset;
    }

    public String getPath() {
        return path;
    }
}
