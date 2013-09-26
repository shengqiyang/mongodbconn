package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeGen {
	
	public void genNodes(String inFile, String outFile) {
		Set<String> stopSet = StringUtil.getStopWords();
		Set<String> ascSet = StringUtil.asc2Set();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), "UTF-8"));
			String line;
			int lnum = 0;
			int pid = 0;
			Map<String, Set<String>> map = new HashMap<String, Set<String>>();
			while ((line = br.readLine()) != null) {
				if (++lnum % 10000000 == 0)
					System.out.println(lnum);
				
				String[] tokens = line.split(" ");
				int id = Integer.parseInt(tokens[0]);
				if (id != pid) {
					if (pid != 0)
						output(pid, map, pw);
					map.clear();
					pid = id;
				}
				
				if (!map.containsKey(tokens[1]))
					map.put(tokens[1], new HashSet<String>());
				map.get(tokens[1]).add(tokens[2]);
				
				if (tokens[1].equals("comment")) {
					Set<String> keywords = genKeywords(ascSet, stopSet,
							tokens[2]);
					if (keywords.size() > 0)
						map.put("keywords", keywords);
				} else if (tokens[1].equals("title"))
					genName(map);
			}
			if (pid != 0 && map.size() > 0)
				output(pid, map, pw);
			br.close();
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * extract keywords from comment (remove stop words)
	 */
	private Set<String> genKeywords(Set<String> ascSet, Set<String> stopSet,
			String comment) {
		Set<String> keywords = new HashSet<String>();
		String[] tokens = comment.split("_");
		for (int i = 0; i < tokens.length; i++) {
			String s = StringUtil.wordDenoise(tokens[i]);
			if (s.length() > 0 && s.length() <= 30 && !stopSet.contains(s)
					&& StringUtil.isAscWord(ascSet, s)
					&& !StringUtil.isNumeric(s) && !StringUtil.isDate(s))
				keywords.add(s);
		}
		return keywords;
	}

	private void genName(Map<String, Set<String>> map) {
		if (!map.containsKey("title"))
			return;
		String name = map.get("title").iterator().next();
		if (name.endsWith(")")) {
			int index = name.lastIndexOf("(");
			if(index > 0)
				name = name.substring(0, index);
			name = StringUtil.wordDenoise(name);
		}
		Set<String> set = new HashSet<String>();
		set.add(name);
		map.put("name", set);
	}

	private void output(int id, Map<String, Set<String>> map, PrintWriter pw) {
		pw.println("id " + id);
		for (String att : map.keySet()) {
			if(att.equals("id"))
				continue;
			pw.print(att);
			Set<String> set = map.get(att);
			for (String val : set) {
				if(att.equals("subject") && val.startsWith("category:"))
					pw.print(" " + val.substring(9));
				else 
					pw.print(" " + val);
			}
			pw.println();
		}
		pw.println();
	}

	public static void main(String[] args) {
		String inFile = "C:\\datasets\\dbpedia\\attributes.txt";
		String outFile = "C:\\datasets\\dbpedia\\nodes.txt";
		NodeGen gen = new NodeGen();
		gen.genNodes(inFile, outFile);
	}
}
