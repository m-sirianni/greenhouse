import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResetHandler implements HttpHandler {

	@SuppressWarnings("deprecation")
	@Override
	public void handle(HttpExchange he) throws IOException {
		System.out.println("Reset signal received");
		String resp = "OK";
		he.sendResponseHeaders(200, resp.length());
		OutputStream os = he.getResponseBody();
		os.write(resp.toString().getBytes());
		os.close();

		for(Thread t : Main.thread_list)
			t.stop();
		Main.thread_list = new ArrayList<Thread>();
		System.out.println("Stopped secondary threads");
		
		Main.main_task.stop();
		System.out.println("Stopped main task");

		Main.A = 0.11;
		System.out.println("A set to default");

		Main.B = 0.0046;
		System.out.println("B set to default");

		Main.MQTT_IP = "193.206.55.23";
		System.out.println("IP set to default");

		Main.main_task = new Thread(Main.main_runnable);
		Main.main_task.start();
		System.out.println("System has been restarted successfully");
	}

}
