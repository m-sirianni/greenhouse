import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TimerRunnable implements Runnable {
	private SubjectTable st;

	public TimerRunnable() {
		this.st = Main.st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe(Main.ROOT_NAME + "/tt", mq);
		JSONParser parser = new JSONParser();
		
		while(true) {
			Message m = null;
			try { m = mq.receive(); } catch (InterruptedException e) {}
			JSONArray jasone = null;
			try { jasone = (JSONArray) parser.parse(m.getBody()); } catch (ParseException e) {}
			
			String coltura = null;
			double time = 0;
			for(Object o : jasone) {
				JSONObject serra = (JSONObject) o;
				coltura = (String) serra.get("coltura");
				time = Double.parseDouble((String) serra.get("time"));
			}
			
			String coltura1 = coltura;
			try {
				st.notify_msg(new Message("TT", Main.ROOT_NAME+"/pt", "[{ \"coltura\" : \"" + coltura1 + "\", \"cmd\" : \"ON\"}]"));
			} catch (InterruptedException e1) {}
			
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

			Runnable task = () -> {
				try {
					st.notify_msg(new Message("TT", Main.ROOT_NAME+"/pt", "[{ \"coltura\" : \"" +coltura1+ "\", \"cmd\" : \"OFF\"}]"));
				} catch (InterruptedException e) {}
			};
			
			executor.schedule(task, (long) time, TimeUnit.SECONDS);
		}

	}
}