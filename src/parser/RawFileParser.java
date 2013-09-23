package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RawFileParser {
	public static final String resPre = "<http://dbpedia.org/resource/";
	public static final String schemaPre = "<http://www.w3.org/2000/01/rdf-schema#";
	public static final String orgPre = "<http://schema.org/";
	public static final String geoPre = "<http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String termPre = "<http://purl.org/dc/terms/";
	public static final String typePre = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String ontPre = "<http://dbpedia.org/ontology/";
	public static final String foafPre = "<http://xmlns.com/foaf/0.1/";

	public void rawParse(String inFile, String outFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), "UTF-8"));
			String line;
			int lnum = 0;
			while ((line = br.readLine()) != null) {
				if (++lnum % 10000000 == 0)
					System.out.println(lnum);

				String[] tokens = line.split(" ");

				String subject = null;
				if (tokens[0].startsWith(resPre))
					subject = tokens[0].substring(resPre.length(),
							tokens[0].length() - 1).toLowerCase();
				else
					continue;

				String predicate = null;
				if (tokens[1].startsWith(ontPre))
					predicate = tokens[1].substring(ontPre.length(),
							tokens[1].length() - 1).toLowerCase();
				else if (tokens[1].startsWith(foafPre))
					predicate = tokens[1].substring(foafPre.length(),
							tokens[1].length() - 1).toLowerCase();
				else if (tokens[1].startsWith(schemaPre))
					predicate = tokens[1].substring(schemaPre.length(),
							tokens[1].length() - 1).toLowerCase();
				else if (tokens[1].startsWith(termPre))
					predicate = tokens[1].substring(termPre.length(),
							tokens[1].length() - 1).toLowerCase();
				else if (tokens[1].startsWith(geoPre))
					predicate = tokens[1].substring(geoPre.length(),
							tokens[1].length() - 1).toLowerCase();
				else if (tokens[1].startsWith(typePre))
					predicate = tokens[1].substring(typePre.length(),
							tokens[1].length() - 1).toLowerCase();
				else
					continue;

				boolean isR = true;
				String object = null;
				if (tokens[2].startsWith(resPre))
					object = tokens[2].substring(resPre.length(),
							tokens[2].length() - 1).toLowerCase();
				else if (tokens[2].startsWith(ontPre))
					object = tokens[2].substring(ontPre.length(),
							tokens[2].length() - 1).toLowerCase();
				else if (tokens[2].startsWith(orgPre))
					object = tokens[2].substring(orgPre.length(),
							tokens[2].length() - 1).toLowerCase();
				else if (tokens[2].startsWith("\"")) {
					String ns = "";
					for (int i = 2; i < tokens.length; i++)
						ns = ns + tokens[i] + "_";
					ns = ns.substring(0, ns.length() - 1);
					int index = ns.indexOf('\"', 1);
					if (index <= 1)
						continue;
					object = ns.substring(1, index).toLowerCase();
					isR = false;
				} else
					continue;
				
				if(isR)
					pw.println(subject + " " + predicate + " " + object + " r");
				else
					pw.println(subject + " " + predicate + " " + object + " p");
			}
			br.close();
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String inFile = "F:\\datasets\\dbpedia\\raw\\mappingbased_properties_en.ttl";
		String outFile = "F:\\datasets\\dbpedia\\raw\\mappingbased_properties_en.txt";
		RawFileParser fp = new RawFileParser();
		fp.rawParse(inFile, outFile);
	}
}
