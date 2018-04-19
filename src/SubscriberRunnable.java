
public class SubscriberRunnable implements Runnable {
	private SubjectTable st;

	public SubscriberRunnable(SubjectTable st) {
		this.st=st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe("root/st", mq);
		while(true) {
			
		}
	}

}