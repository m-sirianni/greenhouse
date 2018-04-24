import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ParsingThread implements Runnable {
	String json;
	ArrayList<String> list;
	public ParsingThread(String st, ArrayList<String> list) {
		json=st;
		this.list = list;
	}
	
	public void run() {
		json = json.trim();
		json = json.replaceAll("\"list\": ", "");
		json = json.substring(1, json.length()-1);
		//a.add(io);
		JSONParser parser = new JSONParser();
		JSONArray o = null;
		try {
			o = (JSONArray) parser.parse(json);
		} catch (ParseException e) {System.out.println("Eccezione");}
		
		
		for (Object ob : o) {
			Set<?> str =( (JSONObject) ob).keySet();
			if(!str.toArray()[0].equals("Temperatura") && !str.toArray()[0].equals("Radiazione")) {
				list.add("humidity/" + str.toArray()[0]);
				//System.out.println("Added humidity/" + str);
				//System.out.println(list.size());
			}
			else {
				list.add((String) str.toArray()[0]);
				//System.out.println("Added " + str);
			}
		}
		//System.out.println("Parsing completato");
	}
	
}

