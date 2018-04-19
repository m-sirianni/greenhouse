import java.io.FileReader;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TimerRunnable implements Runnable {
	private SubjectTable st;

	public TimerRunnable(SubjectTable st) {
		this.st=st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe("root/tt", mq);
		JSONParser parser = new JSONParser();
		Timer timer = new Timer();
		
		while(true) {
			Message m = null;
			try {
				m = mq.receive();
			} catch (InterruptedException e) {}
			
			JSONArray jasone = null;
			try {
				jasone = (JSONArray) parser.parse(m.getBody());
			} catch (ParseException e) {}
			
			String uri = null;
			long time = 0;
			for(Object o : jasone) {
				JSONObject serra = (JSONObject) o;
				
				uri = (String) serra.get("uri");
				time = (long) serra.get("time");
			}
			
			String uri1 = uri;
			// meglio usare il timestamp?
			if(LocalTime.now().getHour() < 8 || LocalTime.now().getHour() >= 18 ) {
				try {
					st.notify_msg(new Message("TT", "root/pt", "[{ \"uri\" : \"" + uri1 + "\", \"cmd\" : \"on\"}]"));
				} catch (InterruptedException e1) {}
				
				Runnable tt = () -> { 
					try {
						st.notify_msg(new Message("TT", "root/pt", "[{ \"uri\" : \"" + uri1 + "\", \"cmd\" : \"off\"}]"));
					} catch (InterruptedException e) {}
				};
				timer.schedule((TimerTask) tt, time);
			}
			
		}

	}
}