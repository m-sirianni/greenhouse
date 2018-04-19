import java.util.ArrayList;

public class Subject {
	public String name;
	public ArrayList<MessageQueue> subscriber;
	
	public Subject(String name) {
		this.name = name;
		subscriber = new ArrayList<MessageQueue>();
	}
}
