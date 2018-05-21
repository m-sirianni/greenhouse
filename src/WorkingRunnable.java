import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WorkingRunnable implements Runnable {
	private SubjectTable st;
	JSONParser parser = new JSONParser();

	public WorkingRunnable(SubjectTable st) {
		this.st=st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe(HTTPServer.ROOT_NAME + "/wt", mq);
		Message m = null;
		while(true) {
			try {
				// Messaggio del tipo:
				// Method: st
				// URI: "root/wt"
				// Body: [{"uri": "root/serre/...", "temp": "30", "umi": "70", "rad", "1500"}]
				m = mq.receive();
			} catch (InterruptedException e) {}
			
			JSONArray jasone = null;
			try {
				jasone = (JSONArray) parser.parse(m.getBody());
			} catch (ParseException e) {}
			
			float temp = 0, hum_morn = 0, rad = 0, hum_even = 0;
			String coltura =null;
			
			for(Object o : jasone) {
				JSONObject serra = (JSONObject) o;
				coltura = (String) serra.get("coltura");
				temp = Float.parseFloat((String) serra.get("temp"));
				rad = Float.parseFloat((String) serra.get("rad"));
				hum_morn = Float.parseFloat((String) serra.get("hum_morn"));
				hum_even = Float.parseFloat((String) serra.get("hum_even"));
				
			}
			// if controllo umidita` vero {
			float time = calculateTime(coltura, temp, hum_morn, hum_even, rad);
			if(time!=0){
				try {
				st.notify_msg(new Message("WT", HTTPServer.ROOT_NAME+"/tt", "[{ \"coltura\" : \"" + coltura + "\", \"time\" : \"" + time + "\"}]"));
				} catch (InterruptedException e) {}
			//System.out.println(time);
			}
			
		}
	}
	
	public long calculateTime(String coltura, float temp, float hum_m, float hum_e, float rad) {
		long time = 0;
		float i;
		float litritot;
		if (coltura.equals("Patate")) {
			i = 45 - hum_e;
			if(i>0){
				litritot = (float)1.13 *i;
				time = (long) ((litritot/15)*60);
				//System.out.println(time);
			}
		}
		
		if (coltura.equals("Cavoli")) {
			i = 65 - hum_e;
			if(i>0){
				litritot = (float)1.1 *i;
				time = (long) ((litritot/10)*60);
			}
		}

		if (coltura.equals("Pomodori")) {
			i = 55 - hum_e;
			if(i>0){
				litritot = (float)0.8 *i;
				time = (long) ((litritot/10)*60);
			}
		}
		
		if (coltura.equals("Piselli")) {
			i = 60 - hum_e;
			if(i>0){
				litritot = (float)0.68 *i;
				time = (long) ((litritot/10)*60);
			}
		}
		return time;
		
	}

}
