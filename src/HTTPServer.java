import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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



public class HTTPServer {
	
	public final static String ROOT_NAME = "serre2";
	
	public static void main(String[] args) throws IOException, ParseException, MqttException, InterruptedException{

		/*
		HttpServer server = HttpServer.create(new InetSocketAddress(10046), 0);
		System.out.println("Server pronto sulla porta " + 10046);
		server.createContext("/", new RootHandler());
		server.createContext("/echoHeader", new EchoHeaderHandler());
		server.createContext("/echoPost", new EchoPostHandler());
		server.setExecutor(null);
		server.start();
		*/
		SubjectTable st = new SubjectTable();

		
		
		PublisherRunnable publisher = new PublisherRunnable(st);
		Thread pub_th = new Thread(publisher);
		pub_th.start();
		TimerRunnable timer = new TimerRunnable(st);
		Thread ti_th = new Thread(timer);
		ti_th.start();
		WorkingRunnable working = new WorkingRunnable(st);
		Thread wt_th = new Thread(working);
		wt_th.start();
		
		MqttClient client = new MqttClient( 
			    "tcp://193.206.55.23:1883",
			    MqttClient.generateClientId(),
			    new MemoryPersistence());
				
		client.setCallback(new MqttCallback() {
			@Override
            public void messageArrived(String topic, MqttMessage message) throws ParseException, InterruptedException {
				String io = message.toString();
				ArrayList<String> list = new ArrayList<String>();
				ParsingThread pt = new ParsingThread(io, list, topic);
				Thread th = new Thread(pt);
				th.start();
				th.join();
				for(String str : list) {
					st.subscribe(HTTPServer.ROOT_NAME + "/" + str, new MessageQueue());
					if (str.contains("humidity")) {
						Thread sub_th = new Thread(new SubscriberRunnable(st, str.split("/")[1]));
						//sub_th.start();
						//Thread.sleep(1000);
					
						//st.notify_msg(new Message("main", HTTPServer.ROOT_NAME+"/st_Patate" , "[{\"cmd\" : \"start\"}]"));
					}
				}
				
			
            }

			@Override
			public void connectionLost(Throwable arg0) {}

			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {}
		});
		
		client.connect();
		client.subscribe(HTTPServer.ROOT_NAME +"/wgetr");
		client.publish(HTTPServer.ROOT_NAME +"/wget/t1234", new MqttMessage("DynamicPage.json".getBytes()));
		Thread.sleep(2000);
		//st.notify_msg(new Message("main", HTTPServer.ROOT_NAME+"/st_Cavoli" , "[{\"cmd\" : \"start\"}]"));
		//Thread.sleep(120000);
		//st.notify_msg(new Message("main", HTTPServer.ROOT_NAME+"/st_Cavoli" , "[{\"cmd\" : \"stop\"}]"));
		st.notify_msg(new Message("st", HTTPServer.ROOT_NAME+"/wt" , "[{ \"coltura\" : \"Patate\", \"temp\" :\"16.24722\" , \"rad\" : \"2205.8425\" , \"hum_morn\" : \"99.95362\" , \"hum_even\" : \"0.0\" }]"));
		st.notify_msg(new Message("st", HTTPServer.ROOT_NAME+"/wt" , "[{ \"coltura\" : \"Piselli\", \"temp\" :\"16.24722\" , \"rad\" : \"2205.8425\" , \"hum_morn\" : \"99.95362\" , \"hum_even\" : \"0.0\" }]"));
		//st.printTable();

		
	}	

	
}