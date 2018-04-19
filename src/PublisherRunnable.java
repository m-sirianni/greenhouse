
public class PublisherRunnable implements Runnable {
	private SubjectTable st;

	public PublisherRunnable(SubjectTable st) {
		this.st=st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe("root/pt", mq);
		while(true) {
			
		}
	}

}