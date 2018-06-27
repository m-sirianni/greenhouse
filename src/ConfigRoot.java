import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ConfigRoot implements HttpHandler {
	@Override
	public void handle(HttpExchange he) throws IOException {
		File index = new File("/home/Scounts/Documenti/Informatica/Java/workspace/greenhouse-master/greenhau5/load_config.html");
		he.sendResponseHeaders(200, index.length());
		OutputStream os = he.getResponseBody();
		Files.copy(index.toPath(), os);
		os.close();
	}
}
