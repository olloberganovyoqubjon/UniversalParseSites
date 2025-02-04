package uz.sites.universalparsesites.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RandomUserAgent {

	public String getRandomUserAgent() throws IOException {

		int rand = (int) (Math.random() * 841);
		return Files.readAllLines(Paths.get("chrome.txt")).get(rand);
	}
}
