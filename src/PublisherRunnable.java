import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PublisherRunnable implements Runnable {
	private SubjectTable st;

	public PublisherRunnable() {
		this.st = Main.st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe(Main.ROOT_NAME+"/pt", mq);
		JSONParser parser = new JSONParser();
		MqttClient client = null;
			
		try {
			client = new MqttClient( 
				    "tcp://" +Main.MQTT_IP+ ":1883",
				    MqttClient.generateClientId(),
				    new MemoryPersistence());
		} catch (MqttException e1) {}
		
		try {
			client.connect();
		} catch (MqttException e1) {}
		while(true) {
			
				Message m = null;
				try {
					m = mq.receive();
				} catch (InterruptedException e) {}
				
				JSONArray jasone = null;
				try {
					jasone = (JSONArray) parser.parse(m.getBody());
				} catch (ParseException e) {}
				
				String coltura = null, cmd = null;
				for(Object o : jasone) {
					JSONObject serra = (JSONObject) o;
					
					coltura = (String) serra.get("coltura");
					cmd = (String) serra.get("cmd");
				}
				
				try {
					client.publish(Main.ROOT_NAME +"/control/set", new MqttMessage(("{\"" + coltura + "\":\""+ cmd +"\"}").getBytes()));
				} catch (MqttException e) {}
			
			
		}
	}

}