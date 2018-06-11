import java.util.ArrayList;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ParsingThread implements Runnable {
	String json;
	ArrayList<String> list;
	String topic;
	Float val;
	
	public ParsingThread(String st, ArrayList<String> list, String topic) {
		json=st;
		this.list = list;
		this.topic = topic;
	}

	public void run() {
		JSONParser parser = new JSONParser();
		JSONArray o = null;
		JSONObject obj = null;
	
		if((Main.ROOT_NAME + "/wgetr").equals(topic)) {
			json = json.trim();
			json = json.replaceAll("\"list\": ", "");
			json = json.substring(1, json.length()-1);

			try {
				o = (JSONArray) parser.parse(json);
			} catch (ParseException e) {}
			
			
			for (Object ob : o) {
				Set<?> str =( (JSONObject) ob).keySet();
				if(!str.toArray()[0].equals("Temperatura") && !str.toArray()[0].equals("Radiazione"))
					list.add((String) "humidity/" + str.toArray()[0]);
				else 
					list.add((String) str.toArray()[0]);
			}
		}
		
		if((Main.ROOT_NAME + "/Temperatura").equals(topic) ||
		   (Main.ROOT_NAME + "/Radiazione").equals(topic)) {
			try {
				obj = (JSONObject) parser.parse(json);
			} catch (ParseException e) {}

				String str = (String) obj.get("value");
				list.add(str);
		}
		
		if(topic.contains(Main.ROOT_NAME + "/humidity")) {
			try {
				obj = (JSONObject) parser.parse(json);
			} catch (ParseException e) {}
			
				String str = (String) obj.get("humidity");
				list.add(str);
		}		
	}	
}