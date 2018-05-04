import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
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

	public SubscriberRunnable(SubjectTable st, String coltura) {
		this.st=st;
		this.coltura=coltura;
		humidity_morn = 0;
		humidity_even = 0;
		temp = 0;
		rad = 0;
		cont = 0;
	}
	
	public void run() {
		JSONParser parser = new JSONParser();
		MessageQueue mq = new MessageQueue();
		Calendar cal = Calendar.getInstance();
		
		st.subscribe(HTTPServer.ROOT_NAME+"/st_" + coltura, mq);
		while(true) {
			Message m = null;
			MqttClient client = null;
			try {
				client = new MqttClient( 
					    "tcp://193.206.55.23:1883",
					    MqttClient.generateClientId(),
					    new MemoryPersistence());
			} catch (MqttException e) {}
					
			client.setCallback(new MqttCallback() {
				@Override
	            public void messageArrived(String topic, MqttMessage message) throws InterruptedException {
					
					String io = message.toString();
					Float val = (float) 0;
					ArrayList<String> ar = new ArrayList<String>();					
					
					if(topic.contains("Temperatura")){
						ParsingThread pt = new ParsingThread(io, ar, topic);
						Thread th = new Thread(pt);
						th.start();
						th.join();
						if(cal.get(Calendar.HOUR_OF_DAY) < 18 && cal.get(Calendar.HOUR_OF_DAY) >= 8 ) {						
							float f = Float.parseFloat(ar.remove(0));
							temp += f;
							cont++;
						}
					}
					
					if(topic.contains("humidity")){
						ParsingThread pt = new ParsingThread(io, ar, topic);
						Thread th = new Thread(pt);
						th.start();
						th.join();
						if(hour==8 && min == 0)
							humidity_morn = val;
						if(hour==18 && min == 0){
							humidity_even = val;
							flag= true;
						}
					}
					
					if(topic.contains("Radiazione")){
						ParsingThread pt = new ParsingThread(io, ar, topic);
						Thread th = new Thread(pt);
						th.start();
						th.join();
						if(cal.get(Calendar.HOUR_OF_DAY) < 18 && cal.get(Calendar.HOUR_OF_DAY) >= 8 ){
							float f = Float.parseFloat(ar.remove(0));
							rad += f;
						}
						
					}
					
					if(cont == 25){
						flag = true;
						temp_media = temp/cont;
						temp = temp_media;
						rad_media = rad/cont;
						rad = rad_media;
						cont = 1;
						System.out.println("temperatura del sole " + temp + " " + coltura);
						System.out.println("radiazioni gamma " + rad + " " + coltura);
					}
				
	            }

				@Override
				public void connectionLost(Throwable arg0) {}

				@Override
				public void deliveryComplete(IMqttDeliveryToken arg0) {}
			});
			
			try {
				m = mq.receive();
			} catch (InterruptedException e) {}
			
			JSONArray jasone = null;
			try {
				jasone = (JSONArray) parser.parse(m.getBody());
			} catch (ParseException e) {}
			
			String cmd = null;
			
			for(Object o : jasone) {
				JSONObject serra = (JSONObject) o;
				
				cmd = (String) serra.get("cmd");
				
			}
			
			if(cmd.equals("start")){
				try {
					client.connect();
				} catch (MqttSecurityException e) {} 
					catch (MqttException e) {}
				try {
					client.subscribe(new String [] {HTTPServer.ROOT_NAME+"/Temperatura", HTTPServer.ROOT_NAME+"/Radiazione", HTTPServer.ROOT_NAME+"/humidity/"+coltura});
				} catch (MqttException e) {}
				cal.setTimeInMillis(m.timeStamp.getTime());
				hour = cal.get(Calendar.HOUR_OF_DAY);
				min = cal.get(Calendar.MINUTE);
			}
			
			if(cmd.equals("stop")){
				cal.setTimeInMillis(m.timeStamp.getTime());
				hour = cal.get(Calendar.HOUR_OF_DAY);
				min = cal.get(Calendar.MINUTE);
			}
			
			if(flag==true){
				//flag = false;
				Message mex = new Message("ST", HTTPServer.ROOT_NAME+"/wt", "[{ \"coltura\" : \"" + coltura + "\", \"temp\" :\"" + temp_media + "\" , \"rad\" : \"" + rad_media + "\" , \"hum_morn\" : \"" + humidity_morn + "\" , \"hum_even\" : \"" + humidity_even + "\" }]");
				System.out.println(mex.body);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		
		}
	}

}