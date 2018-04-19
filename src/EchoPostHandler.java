import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EchoPostHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();
		//parseQuery(query, parameters);
		
		String responzo = "";
		for (String key : parameters.keySet())
			responzo += key + " = " + parameters.get(key) + "\n";
		he.sendResponseHeaders(200, responzo.length());
		OutputStream os = he.getResponseBody();
		os.write(responzo.toString().getBytes());
		os.close();
		
	}

}
