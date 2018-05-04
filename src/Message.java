import java.sql.Timestamp;

public class Message {
	String method;
	String uri;
	String body;
	Timestamp timeStamp;
	
	public Message(String method, String uri, String body) {
		this.method = method;
		this.uri = uri;
		this.body = body;
		timeStamp = new Timestamp(System.currentTimeMillis());
	
	}
	
	public String toString() {
		return "[" + method + " " + uri + "]" + "[" + timeStamp.toString() + "]" + " " + body;
	}
	
	public String getBody() {
		return body;
	}
	
	
}
