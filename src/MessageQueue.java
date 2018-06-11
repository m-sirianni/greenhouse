import java.util.LinkedList;

public class MessageQueue {
	private LinkedList<Message> queue;
	private String threadId;
	int SIZE = 10;

	public MessageQueue() { queue = new LinkedList<Message>(); }
	
	public synchronized void send(Message m) throws InterruptedException {
		queue.add(m);
		if (queue.size() > SIZE)
			queue.poll();
		if (queue.size() == 1)
			this.notifyAll();	
	}

	public synchronized Message receive() throws InterruptedException {
		while (queue.isEmpty())
			this.wait();	
		return queue.poll();
	}
	
	public String getThId() { return threadId; }
		
}