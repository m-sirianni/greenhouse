import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SubscriberRunnable implements Runnable {
	private SubjectTable st;
	private String coltura;
	private float temp;
	private float rad;
	private float temp_media;
	private float rad_media;
	private int  cont;
	
	public SubscriberRunnable(SubjectTable st, String coltura) {
		this.st=st;
		this.coltura=coltura;
		temp = rad = cont = 0;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe(Main.ROOT_NAME+"/st_" + coltura, mq);
	
		MqttClient client = null;
		try {
			client = new MqttClient( 
				    "tcp://" +Main.MQTT_IP+ ":1883",
				    MqttClient.generateClientId(),
				    new MemoryPersistence());
		} catch (MqttException e) {}
		
		try {
			client.connect();
			client.subscribe(new String [] {Main.ROOT_NAME+"/Temperatura", Main.ROOT_NAME+"/Radiazione"});
		} catch (MqttException e1) {}
		
		client.setCallback(new MqttCallback() {
			@Override
	        public void messageArrived(String topic, MqttMessage message) throws InterruptedException {
				JSONParser parser2 = new JSONParser();
				String io = message.toString();
				JSONObject obj = null;
				LocalTime l = LocalTime.now(ZoneId.systemDefault());
				float val = 0;
				try {
					obj = (JSONObject) parser2.parse(io);
					val = Float.parseFloat((String) obj.get("value"));
				} catch (Exception e) { System.out.println( e.getMessage() ); }
				if(topic.contains("Temperatura")){
					if(l.isAfter(Main.MORNING) && l.isBefore(Main.EVENING)) {						
						temp += val;
						cont++;
					}
				}
					
				if(topic.contains("Radiazione")){
					if(l.isAfter(Main.MORNING) && l.isBefore(Main.EVENING))
						rad += val;
				}
					
				if(cont == 10){
					temp_media = temp/cont;
					temp = temp_media;
					rad_media = rad/cont;
					rad = rad_media;
					cont = 1;
				}				
			}

			@Override
			public void connectionLost(Throwable arg0) {}
			@Override
			public void deliveryComplete(IMqttDeliveryToken arg0) {}
				
		});

		LocalDateTime localNow = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
		ZonedDateTime zonedNext5 ;
		zonedNext5 = zonedNow.withHour(18).withMinute(00).withSecond(00);
		if(zonedNow.compareTo(zonedNext5) > 0)
			zonedNext5 = zonedNext5.plusDays(1);

		Duration duration = Duration.between(zonedNow, zonedNext5);
		long initalDelay = duration.getSeconds();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);            
		      
		Runnable task = () -> {
			try {
				System.out.println("Temperatura media della giornata: " + temp_media);
				System.out.println("Radiazione media della giornata: " + rad_media);
				st.notify_msg(new Message("ST", Main.ROOT_NAME+"/wt", "[{ \"coltura\" : \"" + coltura + "\", \"temp\" :\"" + temp_media + "\" , \"rad\" : \"" + rad_media + "\" }]"));
				rad=temp=cont=(int) (temp_media=rad_media=0);	
			} catch (InterruptedException e) {}
		};
				
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(task, initalDelay,24*60*60, TimeUnit.SECONDS);
}
}