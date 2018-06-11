
public class SubjectTable {
	
	private Node<Subject> root;
	
	public  SubjectTable() {
		root = new Node<Subject>(new Subject(Main.ROOT_NAME));
	}
	
	public Node<Subject> getRoot(){
		return root;
	}
	
	public synchronized void subscribe (String url, MessageQueue mq){
		String nodes[] = url.split("/");
		Node<Subject> curLvl = root;
		
		for (int i = 1; i < nodes.length; i++) {
			if (curLvl.getChildren().isEmpty()) {
				Node<Subject> child = new Node<Subject>(new Subject(nodes[i]));
				curLvl.addChild(child);
				curLvl = child;
			}
			
			else {
				boolean flag = false;
				
				for(Node<Subject> s : curLvl.getChildren()) {
					if (s.getData().name.equals(nodes[i]) ) {
						flag = true;
						curLvl = s;
					}
				}
				
				if (!flag) {
					Node<Subject> child = new Node<Subject>(new Subject(nodes[i]));
					curLvl.addChild(child);
					curLvl = child;	
				}	
			}
			
			if (i == nodes.length-1)
				curLvl.getData().subscriber.add(mq);	
			rec_add(root, url, mq, 0);
		}
	
	}
	
	
	private void rec_add(Node<Subject> n, String url, MessageQueue mq, int j){
		String nodes[] = url.split("/");
		for(Node<Subject> child : n.getChildren()) {
			if(child.getData().name.equals(nodes[j+1])) {
				if(j+1 == nodes.length-1){
					if(!child.getData().subscriber.contains(mq)) {
						child.getData().subscriber.add(mq);	
					}
				}
					
				else
					rec_add(child, url, mq, j+1);
			}
		}	
	}
	
	public Node<Subject> lastNode(Node<Subject> n, String url, MessageQueue mq, int j){
		String nodes[] = url.split("/");
		if(n.getChildren().isEmpty()) return null;
		for(Node<Subject> child : n.getChildren()) {
			if(j+1 == nodes.length-1) {
				if(child.getData().name.equals(nodes[j+1]))
					return child;
				else return null;
			}
			return lastNode(child, url, mq, j+1);
		}
		return null;
		
	}
	
	public synchronized void notify_msg(Message m) throws InterruptedException {
		String nodes[] = m.uri.split("/");
		Node<Subject> curLvl = root;
		
		if (!nodes[0].equals(Main.ROOT_NAME)) return;
		
		for (MessageQueue mq : root.getData().subscriber) {
			mq.send(m);
		}
		
		for(int i = 1; i < nodes.length; i++) {
			
			if (curLvl.getChildren().isEmpty())
				break;
				
			for (Node<Subject> sub : curLvl.getChildren()) {
				
				if(sub.getData().name.equals(nodes[i])) {
					curLvl = sub;
					
					for(MessageQueue mq : curLvl.getData().subscriber)
						mq.send(m);

					break;
				}
					
			}
		}
	}
	
	public void byebye(Node<Subject> node, String threadid) {
		if (node.getChildren().isEmpty())
			return;
		
		for (Node<Subject> sub : node.getChildren()) {
			sub.getData().subscriber.removeIf(b -> b.getThId().equals(threadid));
			byebye(sub, threadid);
		}
		
	}
	
}
