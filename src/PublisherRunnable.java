
public class PublisherRunnable implements Runnable {
	private SubjectTable st;

	public PublisherRunnable(SubjectTable st) {
		this.st=st;
	}
	
	public void run() {
		MessageQueue mq = new MessageQueue();
		st.subscribe(HTTPServer.ROOT_NAME+"/pt", mq);
		while(true) {

			
			
		}
	}

}