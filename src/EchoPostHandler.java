import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.Headers;
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
		System.out.println(query);
		/*String responzo = "";
		for (String key : parameters.keySet())
			responzo += key + " = " + parameters.get(key) + "\n";
		he.sendResponseHeaders(200, responzo.length());
		OutputStream os = he.getResponseBody();
		os.write(responzo.toString().getBytes());
		os.close();*/

		JSONArray jasone = null;
		try {
			jasone = (JSONArray) new JSONParser().parse(query);
		} catch (ParseException e) {}
		
		String coltura = null, cmd = null;
		for(Object o : jasone) {
			JSONObject serra = (JSONObject) o;
			
			coltura = (String) serra.get("coltura");
			cmd = (String) serra.get("cmd");
		}
		
		if(cmd.contains("force")) {
			String new_query = query.replaceAll("_force", "");
			try {
				st.notify_msg(new Message("User", HTTPServer.ROOT_NAME+"/pt", new_query));
			} catch (InterruptedException e) {}
			
		}
		
		Calendar cal = Calendar.getInstance();
		
		if(cal.get(Calendar.HOUR_OF_DAY) >= 18 && cal.get(Calendar.HOUR_OF_DAY) < 8 ) {
			String responzo = "Non e` l'ora adatta di innaffiare";
			he.sendResponseHeaders(200, responzo.length());
			OutputStream os = he.getResponseBody();
			os.write(responzo.toString().getBytes());
			os.close();
		}
		else {
			try {
				st.notify_msg(new Message("User", HTTPServer.ROOT_NAME+"/pt", query));
			} catch (InterruptedException e) {}
		}
	}

}
