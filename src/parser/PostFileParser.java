package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class PostFileParser {

	public void postParse(String mapFile, String inFile, String outFile1,
			String outFile2) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(mapFile), "UTF-8"));
			Map<String, Integer> map = new HashMap<String, Integer>();
			String line;
			int lnum = 0;
			while ((line = br.readLine()) != null) {
				if (++lnum % 10000000 == 0)
					System.out.println(lnum);
				String[] tokens = line.split(" ");
				map.put(tokens[2], Integer.parseInt(tokens[0]));
			}
			br.close();
			System.out.println("map size " + map.size());

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					inFile), "UTF-8"));
			PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outFile1), "UTF-8"));
			PrintWriter pw2 = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outFile2), "UTF-8"));
			lnum = 0;
			while ((line = br.readLine()) != null) {
				if (++lnum % 10000000 == 0)
					System.out.println(lnum);

				String[] tokens = line.split(" ");
				String name = tokens[0];
				if (map.containsKey(name)) {
					String type = tokens[3];
					if (type.equals("p"))
						pw1.println(map.get(name) + " " + tokens[1] + " "
								+ tokens[2]);
					else if (type.equals("r")) {
						String name2 = tokens[2];
						if (map.containsKey(name2))
							pw2.println(map.get(name) + " " + map.get(name2)
									+ " " + tokens[1]);
						else
							pw1.println(map.get(name) + " " + tokens[1] + " "
									+ name2);
					}
				}
			}
			br.close();
			pw1.flush();
			pw1.close();
			pw2.flush();
			pw2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void postParse(String mapFile, String inFile, String outFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(mapFile), "UTF-8"));
			Map<String, Integer> map = new HashMap<String, Integer>();
			String line;
			int lnum = 0;
			while ((line = br.readLine()) != null) {
				if (++lnum % 10000000 == 0)
					System.out.println(lnum);
				String[] tokens = line.split(" ");
				map.put(tokens[2], Integer.parseInt(tokens[0]));
			}
			br.close();
			System.out.println("map size " + map.size());

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					inFile), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), "UTF-8"));
			lnum = 0;
			while ((line = br.readLine()) != null) {
				if (++lnum % 10000000 == 0)
					System.out.println(lnum);

				String[] tokens = line.split(" ");
				String name = tokens[0];
				if (map.containsKey(name)) {
					pw.println(map.get(name) + " " + tokens[1] + " "
							+ tokens[2]);
				}
			}
			br.close();
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String mapFile = "F:\\datasets\\dbpedia\\post\\title.txt";
		String inFile = "F:\\datasets\\dbpedia\\raw\\short_abstracts_en.txt";
		String outFile = "F:\\datasets\\dbpedia\\post\\abstracts.txt";
		// String outFile2 = "F:\\datasets\\dbpedia\\post\\relations.txt";
		PostFileParser fp = new PostFileParser();
		fp.postParse(mapFile, inFile, outFile);
	}
}
