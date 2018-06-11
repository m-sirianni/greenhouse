import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ConfigLoader implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();
		String resp = "OK";
		he.sendResponseHeaders(200, resp.length());
		OutputStream os = he.getResponseBody();
		os.write(resp.toString().getBytes());
		os.close();
		System.out.println(query);

		JSONObject j = null;
		try {
			j = (JSONObject) new JSONParser().parse(query);
			Main.A = (double) j.get("a");
			Main.B = (double) j.get("b");;
			Main.MQTT_IP = (String) j.get("ip");;
		} catch (Exception e) {}

		System.out.println("A: " +Main.A+ "\nB: " +Main.B+ "\nIP: " +Main.MQTT_IP);
		
		Main.main_task = new Thread(Main.main_runnable);
		Main.main_task.start();
	}

}
