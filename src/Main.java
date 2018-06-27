import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.sun.net.httpserver.HttpServer;

public class Main {
	
	public final static String ROOT_NAME = "serre2";
	public static double A;
	public static double B;
	public static String MQTT_IP;
	public static final LocalTime MORNING = LocalTime.of(8, 00, 00);
	public static final LocalTime EVENING = LocalTime.of(18, 00, 00);
	public static Thread main_task;
	public static Runnable main_runnable;
	public static ArrayList<Thread> thread_list = new ArrayList<Thread>();
	public static SubjectTable st = new SubjectTable();
	public static MqttClient client;
	
	public static void main(String[] args) throws IOException, ParseException, MqttException, InterruptedException{

		HttpServer server = HttpServer.create(new InetSocketAddress(10046), 0);
		System.out.println("Server pronto sulla porta " + 10046);
		server.createContext("/", new ConfigRoot());
		server.createContext("/load", new ConfigLoader());
		server.createContext("/reset", new ResetHandler());
		server.createContext("/resources", new ResourcesHandler());
		server.setExecutor(null);
		server.start();

		Main.main_runnable = () -> {
			Thread publisher = new Thread(new PublisherRunnable());
			publisher.start();
			Main.thread_list.add(publisher);
			System.out.println("-> Started publisher");
			
			Thread timer = new Thread(new TimerRunnable());
			timer.start();
			Main.thread_list.add(timer);
			System.out.println("-> Started timer thread");

			Thread working = new Thread(new WorkingRunnable());
			working.start();
			Main.thread_list.add(working);
			System.out.println("-> Started working thread");
	
			client = null;
			try {
				client = new MqttClient( 
					"tcp://" +Main.MQTT_IP+ ":1883",
					MqttClient.generateClientId(),
					new MemoryPersistence());
			} catch (MqttException e) {System.out.println(e.getMessage());}
				
			client.setCallback(new MqttCallback() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws ParseException, InterruptedException, MqttException {
					String io = message.toString();
					String json = io.trim();
					json = json.replaceAll("\"list\": ", "");
					json = json.substring(1, json.length()-1);
					JSONParser parser = new JSONParser();
					JSONArray o = null;
					try {
						o = (JSONArray) parser.parse(json);
					} catch (Exception e) {System.out.println("Eccezziunale veramente");}
					
					ArrayList<String> list = new ArrayList<String>();
					for (Object ob : o) {
						Set<?> str =( (JSONObject) ob).keySet();
						if(!str.toArray()[0].equals("Temperatura") && !str.toArray()[0].equals("Radiazione"))
							list.add((String) "humidity/" + str.toArray()[0]);
						else 
							list.add((String) str.toArray()[0]);
					}
					
					for(String str : list) {
						if (str.contains("humidity")) {
							Thread sub_th = new Thread(new SubscriberRunnable(st, str.split("/")[1]));
							System.out.println("-> Started subscriber_" + str.split("/")[1] + " thread");
							sub_th.start();
							Main.thread_list.add(sub_th);
							//System.out.println(Main.ROOT_NAME+"/st"+ str.split("/")[1]);
							//st.notify_msg(new Message("st", Main.ROOT_NAME+"/st_"+ str.split("/")[1], "{\"cmd\":\"start\"}"));
						}
					}
					client.unsubscribe(Main.ROOT_NAME +"/wgetr");
				}

				@Override
				public void connectionLost(Throwable arg0) {}

				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {}
			});
		
			try {
				client.connect();
				System.out.println("-> MQTT client connected");
				client.subscribe(Main.ROOT_NAME +"/wgetr");
				client.publish(Main.ROOT_NAME +"/wget/t1234", new MqttMessage(("DynamicPage.json").getBytes()));
				
			} catch (MqttException e) {System.out.println(e.getMessage());}
		};

	}
}