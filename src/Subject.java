import java.util.ArrayList;

public class Subject {
	private String name;
	private ArrayList<MessageQueue> subscriber;
	
	public Subject(String name) {
		this.setName(name);
		setSubscriber(new ArrayList<MessageQueue>());
	}

	public ArrayList<MessageQueue> getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(ArrayList<MessageQueue> subscriber) {
		this.subscriber = subscriber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
