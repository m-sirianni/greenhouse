import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange he) throws IOException {
		File index = new File("greenhau5/index.html");
		he.sendResponseHeaders(200, index.length());
		OutputStream os = he.getResponseBody();
		Files.copy(index.toPath(), os);
		os.close();
	}
}
