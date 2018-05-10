import java.io.FileReader;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
		st.subscribe(HTTPServer.ROOT_NAME + "/tt", mq);
		JSONParser parser = new JSONParser();
		Timer timer = new Timer();
		
		while(true) {
			Message m = null;
			try {
				m = mq.receive();
			} catch (InterruptedException e) {}
			System.out.println(m.body);
			JSONArray jasone = null;
			try {
				jasone = (JSONArray) parser.parse(m.getBody());
			} catch (ParseException e) {}
			
			String coltura = null;
			long time = 0;
			for(Object o : jasone) {
				JSONObject serra = (JSONObject) o;
				
				coltura = (String) serra.get("coltura");
				time = (long) Float.parseFloat((String) serra.get("time"));
			}
			
			String coltura1 = coltura;
			// meglio usare il timestamp?
						//if(LocalTime.now().getHour() < 8 || LocalTime.now().getHour() > 18 ) {
			try {
				st.notify_msg(new Message("TT", HTTPServer.ROOT_NAME+"/pt", "[{ \"coltura\" : \"" + coltura + "\", \"cmd\" : \"ON\"}]"));
			} catch (InterruptedException e1) {}
			
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

			Runnable task = () -> {
				try {
					st.notify_msg(new Message("TT", HTTPServer.ROOT_NAME+"/pt", "[{ \"coltura\" : \"" + coltura1 + "\", \"cmd\" : \"OFF\"}]"));
				} catch (InterruptedException e) {}
			};
			
			executor.schedule(task, time, TimeUnit.SECONDS);
			
		}

	}
}