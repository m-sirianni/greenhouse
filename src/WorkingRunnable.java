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
		st.subscribe("root/wt", mq);
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
			
			long temp = 0, umi = 0, rad = 0;
			String uri = null;
			
			for(Object o : jasone) {
				JSONObject serra = (JSONObject) o;
				uri = (String) serra.get("uri");
				temp = (long) serra.get("temp");
				umi = (long) serra.get("umi");
				rad = (long) serra.get("rad");
			}
			// if controllo umidita` vero {
			long time = calculateTime(temp, umi, rad);
			try {
				st.notify_msg(new Message("WT", "root/tt", "[{ \"uri\" : \"" + uri + "\", \"time\" : \"" + time + "\"}]"));
			} catch (InterruptedException e) {}
			
		}
	}
	
	public long calculateTime(long temp, long umi, long rad) {
		return 1;
	}

}
