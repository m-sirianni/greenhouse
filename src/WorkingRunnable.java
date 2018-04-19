
public class WorkingRunnable implements Runnable {
	private SubjectTable st;

	public WorkingRunnable(SubjectTable st) {
		this.st=st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe("root/wt", mq);
		while(true) {
			try {
				Message m = mq.receive();
			} catch (InterruptedException e) {}
			
			
		}
	}

}
