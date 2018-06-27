import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WorkingRunnable implements Runnable {
	private SubjectTable st;
	JSONParser parser = new JSONParser();

	public WorkingRunnable() {
		this.st = Main.st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe(Main.ROOT_NAME + "/wt", mq);
		Message m = null;
		while(true) {
			try { m = mq.receive(); } catch (InterruptedException e) {}
			JSONArray jasone = null;
			try {
				jasone = (JSONArray) parser.parse(m.getBody());
			} catch (ParseException e) {}
			
			float temp = 0, rad = 0;
			String coltura = null;
			
			for(Object o : jasone) {
				JSONObject serra = (JSONObject) o;
				coltura = (String) serra.get("coltura");
				temp = Float.parseFloat((String) serra.get("temp"));
				rad = Float.parseFloat((String) serra.get("rad"));
			}

			double time = calculateTime(temp, rad);
			try {
				st.notify_msg(new Message("WT", Main.ROOT_NAME+"/tt", "[{ \"coltura\" : \"" + coltura + "\", \"time\" : \"" + time + "\"}]"));
			} catch (InterruptedException e) {}
			
		}
	}
	
	public double calculateTime(float temp, float rad) {
		double deltaHum = temp*Main.A + rad*Main.B;
		double time = deltaHum * 2.5;
		return time;		
	}

}
