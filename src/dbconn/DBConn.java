package dbconn;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.StringUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class DBConn {

	public static final String DB_URL = "localhost";
	public static final int DB_PORT = 27017;

	public static final String DB_NAME = "dbpedia";
	public static final String DB_NODE_COLL = "thing";
	public static final String DB_EDGE_COLL = "relations";

	private DB db;

	public DBConn() {
		db = null;
		try {
			MongoClient mongoClient = new MongoClient(DB_URL, DB_PORT);
			db = mongoClient.getDB(DB_NAME);
			if (db != null)
				System.out.println("Connected to: " + db.getName() + " ("
						+ db.getCollectionNames().size() + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadNodes(String inFile) {
		try {
			DBCollection coll = db.getCollection(DB_NODE_COLL);
			if (coll != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(inFile), "UTF-8"));
				String line;
				int lnum = 0;
				Map<String, Set<String>> map = new HashMap<String, Set<String>>();
				while ((line = br.readLine()) != null) {
					if (++lnum % 1000000 == 0)
						System.out.println(lnum);
					if (line.length() > 0) {
						String[] tokens = line.split(" ");
						map.put(tokens[0], new HashSet<String>());
						for (int i = 1; i < tokens.length; i++)
							map.get(tokens[0]).add(tokens[i]);
					} else if (line.length() == 0) {
						BasicDBObject doc = constructDoc(map);
						coll.insert(doc);
						map.clear();
					}
				}
				if (!map.isEmpty()) {
					BasicDBObject doc = new BasicDBObject(map);
					coll.insert(doc);
					map.clear();
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BasicDBObject constructDoc(Map<String, Set<String>> map) {
		BasicDBObject doc = new BasicDBObject();
		for (String field : map.keySet()) {
			List<Object> list = new ArrayList<Object>();
			for (String val : map.get(field)) {
				val = val.replace(",", "");
				if (val.endsWith("\\"))
					val = StringUtil.wordDenoise(val);
				if (val.length() == 0)
					continue;
				else if (StringUtil.isInteger(val)) {
					list.add(Long.parseLong(val));
				} else if (StringUtil.isDouble(val)) {
					list.add(Double.parseDouble(val));
				} else
					list.add(val);
			}
			if (list.size() == 1)
				doc.append(field, list.get(0));
			else if (list.size() > 1)
				doc.append(field, list);
		}
		return doc;
	}

	public void loadEdges(String inFile) {
		try {
			DBCollection coll = db.getCollection(DB_EDGE_COLL);
			if (coll != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(inFile), "UTF-8"));
				String line;
				int lnum = 0;
				while ((line = br.readLine()) != null) {
					if (++lnum % 1000000 == 0)
						System.out.println(lnum);

					String[] tokens = line.split(" ");
					int sid = Integer.parseInt(tokens[0]);
					int tid = Integer.parseInt(tokens[1]);
					BasicDBObject doc = new BasicDBObject();
					doc.append("sid", sid).append("tid", tid)
							.append("relation", tokens[2]);
					coll.insert(doc);
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DBConn dbConn = new DBConn();
		dbConn.loadNodes("c:\\datasets\\dbpedia\\nodes.txt");
		// dbConn.loadEdges("c:\\datasets\\dbpedia\\relations.txt");
	}
}
