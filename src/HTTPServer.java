import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
	
	private static ArrayList<String> serre;
	private static ArrayList<String> url_serre;
	public static void main(String[] args) throws IOException, ParseException, MqttException{
		/*serre = new ArrayList<String>();
		url_serre = new ArrayList<String>();
		
		HttpServer server = HttpServer.create(new InetSocketAddress(10046), 0);
		System.out.println("Server pronto sulla porta " + 10046);
		server.createContext("/", new RootHandler());
		server.createContext("/echoHeader", new EchoHeaderHandler());
		server.createContext("/echoPost", new EchoPostHandler());
		server.setExecutor(null);
		server.start();
		
		SubjectTable st = new SubjectTable();
		parseConf();
		MessageQueue arMq[] = new MessageQueue[serre.size()];
		
		for(int i=0; i<arMq.length ; i++)
			arMq[i] = new MessageQueue();
		
		
		for(int i=0;i<url_serre.size(); i++)
			st.subscribe(url_serre.get(i), arMq[i]);
		
		SubscriberRunnable subscriber = new SubscriberRunnable(st);
		PublisherRunnable publisher = new PublisherRunnable(st);
		TimerRunnable timer = new TimerRunnable(st);
		WorkingRunnable working = new WorkingRunnable(st);
		*/
		MqttClient client = new MqttClient( 
			    "tcp://193.206.55.23:1883", //URI 
			    MqttClient.generateClientId(), //ClientId 
			    new MemoryPersistence()); //Persistence
		
		client.setCallback(new MqttCallback() {
			@Override
            public void messageArrived(String topic, MqttMessage message) {
				JSONParser parser = new JSONParser();
			 	String json = message.toString();
             	String json1 = json;
             	System.out.println(json);
             
               JSONArray jasone = null;
    		   try {
				jasone = (JSONArray) parser.parse(json1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		   System.out.println("caca");
    			
    			
            }
			
			

			@Override
			public void connectionLost(Throwable arg0) {}

			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {}
		});
		
		client.connect();
		client.subscribe("serre2/wgetr");
		client.publish("serre2/wget/t1234", new MqttMessage("DynamicPage.json".getBytes()));
		
		
		
	}

	private static void parseConf() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONArray jasone = (JSONArray) parser.parse(new FileReader("/home/20016240/conf.json"));
		
		for(Object o : jasone) {
			JSONObject serra = (JSONObject) o;
			
			String name = (String) serra.get("name");
			serre.add(name);
			
			String url = (String) serra.get("url");
			url_serre.add(url);
			
		}
		
		
	}
	
}