import java.sql.Timestamp;

public class Message {
	String method;
	String uri;
	String body;
	String timeStamp;
	
	public Message(String method, String uri, String body) {
		this.method = method;
		this.uri = uri;
		this.body = body;
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		timeStamp = ts.toString();
	}
	
	public String toString() {
		return "[" + method + " " + uri + "]" + "[" + timeStamp + "]" + " " + body;
	}
	
	public String getBody() {
		return body;
	}
	
	
}
