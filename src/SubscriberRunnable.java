import java.util.ArrayList;
import java.util.Calendar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SubscriberRunnable implements Runnable {
	private SubjectTable st;
	private String coltura;
	private float humidity_morn;
	private float humidity_even;
	private float temp;
	private float rad;
	private float temp_media;
	private float rad_media;
	private int  cont;
	private int  hour;
	private int  min;
	private boolean  flag = true;
	private Message mex;

	public SubscriberRunnable(SubjectTable st, String coltura) {
		this.st=st;
		this.coltura=coltura;
		temp = 0;
		rad = 0;
		cont = 0;
	}
	
	public void run() {
		JSONParser parser = new JSONParser();
		MessageQueue mq = new MessageQueue();
		Calendar cal = Calendar.getInstance();
		
		st.subscribe(Main.ROOT_NAME+"/st_" + coltura, mq);
		
		Message m = null;
		MqttClient client = null;
		try {
			client = new MqttClient( 
				    "tcp://" +Main.MQTT_IP+ ":1883",
				    MqttClient.generateClientId(),
				    new MemoryPersistence());
		} catch (MqttException e) {}
					
		client.setCallback(new MqttCallback() {
			@Override
	        public void messageArrived(String topic, MqttMessage message) throws InterruptedException {
				JSONParser parser2 = new JSONParser();
				String io = message.toString();
				JSONObject obj = null;
				System.out.println(message.toString());
				float val = 0;
				try {
					obj = (JSONObject) parser2.parse(io);
					val = Float.parseFloat((String) obj.get("value"));
				} catch (Exception e) { System.out.println( e.getMessage() ); }

				if(topic.contains("Temperatura")){
					if(cal.get(Calendar.HOUR_OF_DAY) < Main.EVENING && cal.get(Calendar.HOUR_OF_DAY) >= Main.MORNING) {						
						temp += val;
						cont++;
					}
				}
					
				if(topic.contains("Radiazione")){
					if(cal.get(Calendar.HOUR_OF_DAY) < Main.EVENING && cal.get(Calendar.HOUR_OF_DAY) >= Main.MORNING)
						rad += val;
				}
					
				if(cont == 10){
					temp_media = temp/cont;
					temp = temp_media;
					rad_media = rad/cont;
					rad = rad_media;
					cont = 1;
				}
				
				if(flag == true){
					flag = false;
				    mex = new Message("ST", Main.ROOT_NAME+"/wt", "[{ \"coltura\" : \"" + coltura + "\", \"temp\" :\"" + temp_media + "\" , \"rad\" : \"" + rad_media + "\" , \"hum_morn\" : \"" + humidity_morn + "\" , \"hum_even\" : \"" + humidity_even + "\" }]");
				}
			}

			@Override
			public void connectionLost(Throwable arg0) {}

			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {}
		});

		while (true) {
			try {
				m = mq.receive();
				System.out.println("Messaggio ricevuto");
			} catch (InterruptedException e) {}
		
			JSONObject serra = null;
			try { serra = (JSONObject) parser.parse(m.getBody()); } catch (ParseException e) {}
			
			String cmd = null;
			cmd = (String) serra.get("cmd");
			System.out.println(cmd);
			
			if(cmd.equals("start")) {
				try {
					client.connect();
					client.subscribe(new String [] {Main.ROOT_NAME+"/Temperatura", Main.ROOT_NAME+"/Radiazione"});
				} catch (MqttException e) {}
				
				cal.setTimeInMillis(m.getTimeStamp().getTime());
				hour = cal.get(Calendar.HOUR_OF_DAY);
				min = cal.get(Calendar.MINUTE);
			}
			
			if(cmd.equals("stop")){			
				cal.setTimeInMillis(m.getTimeStamp().getTime());
				hour = cal.get(Calendar.HOUR_OF_DAY);
				min = cal.get(Calendar.MINUTE);
				//try {
					//st.notify_msg(mex);
				//} catch (InterruptedException e) {}
			}
		}
	}

}