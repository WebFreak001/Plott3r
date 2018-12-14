package org.webfreak.plott3r.font;

import java.util.HashMap;

public class Font {
    private HashMap<Character, FontChar> fontMapper = new HashMap<>();
    public Font() {
        fontMapper.put('A', new FontChar("M0,8L3.5,-0M3.5,0L7,8M1.75,4H5.25", 8));
    }

    public FontChar getCharacter(Character character) {
        return fontMapper.get(character);
    }
}
