import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GreenhouseLogger implements Runnable {

	private SubjectTable st;
	private Logger logger;
	private MessageQueue mq;
	
	public GreenhouseLogger(SubjectTable st) {
		this.st = st;
		mq = new MessageQueue();
		logger = Logger.getLogger("Greenhau5 Logger");
	}

	@Override
	public void run() {

		st.subscribe(HTTPServer.ROOT_NAME + "/logger", mq);

		FileHandler fh = null;
		try {
			fh = new FileHandler("greenhouse_logs.txt");
		} catch (SecurityException | IOException e1) {}
		
		fh.setFormatter(new SimpleFormatter());
		logger.addHandler(fh);
		
		while(true) {
			Message m = null;
			try { m = mq.receive(); } catch (InterruptedException e) {}
			logger.info(m.body);
		}
		
	}
}