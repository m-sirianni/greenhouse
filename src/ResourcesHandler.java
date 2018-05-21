import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResourcesHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		File img = new File("/home/20016650/Desktop/EsReti2/greenhau5/" +he.getRequestURI().getPath());
		he.sendResponseHeaders(200, img.length());
		OutputStream os = he.getResponseBody();
		Files.copy(img.toPath(), os);
		os.close();
	}

}