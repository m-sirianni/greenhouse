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
	
	
	public static void main(String[] args) throws IOException, ParseException, MqttException, InterruptedException{
		/*serre = new ArrayList<String>();
		url_serre = new ArrayList<String>();
		
		HttpServer server = HttpServer.create(new InetSocketAddress(10046), 0);
		System.out.println("Server pronto sulla porta " + 10046);
		server.createContext("/", new RootHandler());
		server.createContext("/echoHeader", new EchoHeaderHandler());
		server.createContext("/echoPost", new EchoPostHandler());
		server.setExecutor(null);
		server.start();
		*/
		SubjectTable st = new SubjectTable();
		/*
		parseConf();
		MessageQueue arMq[] = new MessageQueue[serre.size()];
		
		for(int i=0; i<arMq.length ; i++)
			arMq[i] = new MessageQueue();
		
		
		for(int i=0;i<url_serre.size(); i++)
			st.subscribe(url_serre.get(i), arMq[i]);
		*/
		
		//SubscriberRunnable subscriber = new SubscriberRunnable(st);
		//PublisherRunnable publisher = new PublisherRunnable(st);
		//TimerRunnable timer = new TimerRunnable(st);
		//WorkingRunnable working = new WorkingRunnable(st);
		
		//JSONParser parser = new JSONParser();
		MqttClient client = new MqttClient( 
			    "tcp://193.206.55.23:1883", //URI 
			    MqttClient.generateClientId(), //ClientId 
			    new MemoryPersistence()); //Persistence
				
		client.setCallback(new MqttCallback() {
			@Override
            public void messageArrived(String topic, MqttMessage message) throws ParseException, InterruptedException {
				String io = message.toString();
				ArrayList<String> list = new ArrayList<String>();
				ParsingThread pt = new ParsingThread(io, list);
				Thread th = new Thread(pt);
				th.start();
				th.join();
				//System.out.println(list.size());
				//System.out.println(Arrays.toString(list.toArray()));
				for(String str : list) {
					st.subscribe("serre1/" + str, new MessageQueue());
					System.out.println("Sottoscritto a serre1/" + str );
				}
				//st.subscribe(, mq);
			
            }

			@Override
			public void connectionLost(Throwable arg0) {}

			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {}
		});
		
		client.connect();
		client.subscribe("serre1/wgetr");
		client.publish("serre1/wget/t1234", new MqttMessage("DynamicPage.json".getBytes()));
		Thread.sleep(2000);
		//st.subscribe("serre1/humidity/Cavoli", new MessageQueue());
		st.printTable();
		//Node<Subject> root = st.getRoot();
		//st.visita(root);

		
	}

	private static void parseConf(String json) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		
		JSONObject jasone = (JSONObject) parser.parse(json);
		
		
		
		//for(Object o : jasone) {
			
			
			//String temp = (String) jasone.get("Temperatura");
		   // System.out.println(radiazione);
		
		//}
		
		
	}
	

	
}