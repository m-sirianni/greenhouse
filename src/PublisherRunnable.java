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

	public PublisherRunnable(SubjectTable st) {
		this.st=st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe(HTTPServer.ROOT_NAME+"/pt", mq);
		JSONParser parser = new JSONParser();
		MqttClient client = null;
			
		try {
			client = new MqttClient( 
				    "tcp://193.206.55.23:1883",
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
					client.publish(HTTPServer.ROOT_NAME +"/control/set", new MqttMessage(("{\"" + coltura + "\":\""+ cmd +"\"}").getBytes()));
					//System.out.println("{\"" + coltura + "\":\""+ cmd +"\"}");
				} catch (MqttException e) {}
			
			
		}
	}

}