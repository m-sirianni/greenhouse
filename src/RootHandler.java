import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		String responzo = "<h1>FUNZIONAH!!!!1!!!!11</h1>";
		he.sendResponseHeaders(200, responzo.length());
		OutputStream os = he.getResponseBody();
		os.write(responzo.getBytes());
		os.close();
	}

}
