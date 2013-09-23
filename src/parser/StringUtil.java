package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StringUtil {

	public static String ASCII_ENCODE = "files/ascii_encode.txt";
	public static String STOP_WORDS = "files/stop_words.txt";

	public static boolean isDate(String str) {
		return str.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}");
	}

	public static boolean isInteger(String str) {
		if(str.length() > 12)
			return false;
		if (str.length() == 1 && (str.charAt(0) == '-' || str.charAt(0) == '+'))
			return false;
		return str.matches("^[-\\+]?[\\d]*$"); // [-+]?\\d+(\\,\\d+)?(\\.\\d+)?(e\\d+)?
	}

	public static boolean isDouble(String str) {
		if(str.length() > 12)
			return false;
		if (str.length() == 1 && (str.charAt(0) == '-' || str.charAt(0) == '+'))
			return false;
		return str.matches("[-+]?\\d+(\\,\\d+)?(\\.\\d+)?(e\\d+)?"); // [-+]?\\d+(\\,\\d+)?(\\.\\d+)?(e\\d+)?
	}

	public static boolean isNumeric(String str) {
		return isInteger(str) || isDouble(str);
	}

	public static Map<String, String> asc2Map() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(ASCII_ENCODE));
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\t");
				map.put(tokens[1], tokens[0]);
				map.put(tokens[1].toLowerCase(), tokens[0]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static Set<String> asc2Set() {
		Set<String> set = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(ASCII_ENCODE));
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("\t");
				set.add(tokens[0]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}

	public static boolean isAscWord(Set<String> ascSet, String word) {
		boolean is = true;
		for (int i = 0; i < word.length(); i++) {
			if (!ascSet.contains(word.charAt(i) + "")) {
				is = false;
				break;
			}
		}
		return is;
	}

	public static Set<String> getStopWords() {
		Set<String> set = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(STOP_WORDS));
			String line;
			while ((line = br.readLine()) != null) {
				set.add(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}

	private String convert(Map<String, String> map, String s) {
		int i = 0;
		while (i < s.length()) {
			if (s.charAt(i) == '%' && i + 2 < s.length()) {
				String ss = s.substring(i, i + 3);
				if (map.containsKey(ss))
					s = s.replace(ss, map.get(ss));
			}
			++i;
		}
		return s;
	}

	public void ascConvert(String inFile, String outFile) {
		try {
			Map<String, String> map = asc2Map();
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			PrintWriter printer = new PrintWriter(new FileWriter(outFile));
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				String word = convert(map, tokens[0]);
				printer.print(word);
				for (int i = 1; i < tokens.length; i++)
					printer.print(" " + tokens[i]);
				printer.println();
			}
			br.close();
			printer.flush();
			printer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * "(abc!!" -> "abc"
	 */
	public static String wordDenoise(String s) {
		String r = s.trim();
		r = r.replace("(", "");
		r = r.replace(")", "");
		String start = "#~,'*{[_\\ \"";
		String end = ",.;:'-*}?!]_\\ \"";
		int i = 0;
		for (; i < r.length(); i++) {
			if (!start.contains(r.charAt(i) + ""))
				break;
		}
		if (i == r.length())
			return "";
		int j = r.length() - 1;
		for (; j >= i; j--) {
			if (!end.contains(r.charAt(j) + ""))
				break;
		}
		if (j < i)
			return "";
		r = r.substring(i, j + 1).trim();
		if (r.startsWith("http://"))
			return "";
		return r;
	}

	public void wordCleaner(String inFile, String outFile) {
		try {
			Set<String> set = getStopWords();
			BufferedReader br = new BufferedReader(new FileReader(inFile));
			PrintWriter pw = new PrintWriter(new FileWriter(outFile));
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				String s = wordDenoise(tokens[0]);
				if (s.length() > 0 && s.length() <= 50 && !set.contains(s)) {
					pw.print(s);
					for (int i = 1; i < tokens.length; i++)
						pw.print(" " + tokens[i]);
					pw.println();
				}
			}
			br.close();
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * De-duplicate:
	 * 
	 * "earthlings_album album musicalwork work" ->
	 * "earthlings album musicalwork work"
	 */
	public static String denoiseDBpedia(String string) {
		int idx = string.indexOf(' ');
		String s = idx < 0 ? string : string.substring(0, idx);
		String[] tokens = s.split("_");
		if (tokens.length == 1)
			return string;
		else {
			String last = tokens[tokens.length - 1];
			if (string.substring(idx + 1).contains(last))
				return s.substring(0, s.lastIndexOf("_")) + " "
						+ string.substring(idx + 1, string.length());
			else
				return string;
		}
	}

	public static void main(String[] args) {
		boolean d = StringUtil.isInteger("100,123");
		System.out.println(d);
	}
}
