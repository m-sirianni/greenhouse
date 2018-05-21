import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EchoPostHandler implements HttpHandler {
	private SubjectTable st;
	
	public EchoPostHandler(SubjectTable st) {
		this.st = st;
	}

	@Override
	public void handle(HttpExchange he) throws IOException {
		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();

		JSONArray j = null;
		try {
			j = (JSONArray) new JSONParser().parse(query);
		} catch (ParseException e) {}
		
		String coltura = null, cmd = null;
		for(Object o : j) {
			JSONObject serra = (JSONObject) o;
			
			coltura = (String) serra.get("coltura");
			cmd = (String) serra.get("cmd");
		}
		
		if(cmd.contains("force")) {
			String new_query = query.replaceAll("_force", "");
			try {
				st.notify_msg(new Message("User", HTTPServer.ROOT_NAME+"/pt", new_query));
				st.notify_msg(new Message("User", HTTPServer.ROOT_NAME+"/logger", 
						                  "\"" + coltura + "\"" + " switched " + cmd.replaceAll("_force", "").toLowerCase() + " (from JS client)"
				));
			} catch (InterruptedException e) {}
			
		}
		
		Calendar cal = Calendar.getInstance();
		
		if(cal.get(Calendar.HOUR_OF_DAY) < 18 && cal.get(Calendar.HOUR_OF_DAY) >= 8 ) {
			String resp = "Non e` l'ora adatta di innaffiare";
			he.sendResponseHeaders(200, resp.length());
			OutputStream os = he.getResponseBody();
			os.write(resp.toString().getBytes());
			os.close();
		}
		else {
			try {
				st.notify_msg(new Message("User", HTTPServer.ROOT_NAME+"/pt", query));
				st.notify_msg(new Message("User", HTTPServer.ROOT_NAME+"/logger", "\"" + coltura + "\"" + " switched " + cmd.toLowerCase() + " (from JS client)"));
			} catch (InterruptedException e) {}
		}
	}

}
