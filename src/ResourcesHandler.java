import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResourcesHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		File res = new File("greenhau5" +he.getRequestURI().getPath());
		he.sendResponseHeaders(200, res.length());
		OutputStream os = he.getResponseBody();
		Files.copy(res.toPath(), os);
		os.close();
	}

}