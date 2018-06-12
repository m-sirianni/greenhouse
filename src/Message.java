import java.sql.Timestamp;

public class Message {
	private String method;
	private String uri;
	private String body;
	private Timestamp timeStamp;
	
	public Message(String method, String uri, String body) {
		this.setMethod(method);
		this.setUri(uri);
		this.body = body;
		setTimeStamp(new Timestamp(System.currentTimeMillis()));
	}

	public String getBody() { return body; }

	public Timestamp getTimeStamp() { return timeStamp; }

	public void setTimeStamp(Timestamp timeStamp) { this.timeStamp = timeStamp; }

	public String getMethod() { return method; }

	public void setMethod(String method) { this.method = method; }

	public String getUri() { return uri; }

	public void setUri(String uri) { this.uri = uri; }
	
}