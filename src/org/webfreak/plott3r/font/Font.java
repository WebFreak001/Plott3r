package org.webfreak.plott3r.font;

import org.webfreak.plott3r.Main;
import org.webfreak.plott3r.svg.path.Path;
import org.webfreak.plott3r.svg.path.PathParser;
import org.webfreak.plott3r.svg.path.PathSeg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Font {
	private Map<Integer, FontChar> fontMapper = new HashMap<>();

	public Font() {
	}

	public Map<Integer, FontChar> getMap() {
		return fontMapper;
	}

	public void put(Integer character, FontChar info) {
		fontMapper.put(character, info);
	}

	public FontChar getCharacter(Integer character) {
		return fontMapper.get(character);
	}

	public static Font fromFile(File file) throws IOException {
		return fromScanner(new Scanner(file, "utf-8"));
	}

	public static Font fromResource(String name) throws IOException {
		Font f = new Font();
		Scanner scanner = new Scanner(f.getClass().getClassLoader().getResourceAsStream(name), "utf-8");
		return fromScanner(scanner);
	}

	private static Font fromScanner(Scanner scanner) throws IOException {
		Font font = new Font();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("//") || line.charAt(line.offsetByCodePoints(0, 1)) != ' ' || line.isEmpty())
				continue;

			int c = line.codePointAt(0);
			String path = line.substring(line.offsetByCodePoints(0, 2));
			font.put(c, new FontChar(path, 8));
		}

		return font;
	}
}
