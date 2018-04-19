import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EchoHeaderHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		Headers headers = he.getRequestHeaders();
		Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
		String responzo = "";
		
		for (Map.Entry<String, List<String>> entry : entries)
				responzo += entry.toString() + "\n";
		
		he.sendResponseHeaders(200, responzo.length());
		OutputStream os = he.getResponseBody();
		os.write(responzo.toString().getBytes());
		os.close();
	}


}
