package org.webfreak.plott3r.font;

import java.util.HashMap;

public class Font {
    private HashMap<Character, FontChar> fontMapper = new HashMap<>();
    public Font() {
        fontMapper.put('A', new FontChar("M0,8l4,-8l4,8M1,4h4", 8));
    }

    public FontChar getCharacter(Character character) {
        return fontMapper.get(character);
    }
}
