import java.util.Arrays;

public class SubjectTable {
	
	private Node<Subject> root;
	
	public  SubjectTable() {
		root = new Node<Subject>(new Subject("serre1"));
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
				System.out.println("1. Ho creato :" + child.getData().name);
				curLvl.addChild(new Node<Subject>(new Subject(nodes[i])));
				curLvl = child;
				
			}
			
			else {
				boolean flag = false;
				
				
				for(Node<Subject> s : curLvl.getChildren()) {
					if (s.getData().name.equals(nodes[i]) ) {
						System.out.println("Sono su :" + s.getData().name);
						flag = true;
						curLvl = s;
						
					}
				}
				
				if (!flag) {
					Node<Subject> child = new Node<Subject>(new Subject(nodes[i]));
					System.out.println("2. Ho creato :" + child.getData().name);
					curLvl.addChild(new Node<Subject>(new Subject(nodes[i])));
					curLvl = child;
					
				}
				
			}
			
			/*if (i == nodes.length-1) {
				curLvl.getData().subscriber.add(mq);
				//System.out.println(curLvl.getData().name);
				
			}*/
			////rec_add(root, url, mq, 0);
		}
		
		
	}
	
	
	private void rec_add(Node<Subject> n, String url, MessageQueue mq, int j){
		
		String nodes[] = url.split("/");
		for(Node<Subject> child : n.getChildren()) {
			//System.out.println(child.getData().name);
			//System.out.println(nodes[j+1] + "\n");
			if(child.getData().name.equals(nodes[j+1])) {
				if(j+1 == nodes.length-1){
					child.getData().subscriber.add(mq);
					//System.out.println("Mi sono iscritto a :" + child.getData().name);
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
		
		if (!nodes[0].equals("serre1")) return;
		
		for (MessageQueue mq : root.getData().subscriber) {
			mq.send(m);
		}
		
		for(int i = 1; i < nodes.length; i++) {
			
			if (curLvl.getChildren().isEmpty())
				break;
				
			for (Node<Subject> sub : curLvl.getChildren()) {
				
				if(sub.getData().name.equals(nodes[i])) {
					curLvl = sub;
					//System.out.println(curLvl.getData().name + " " + curLvl.getData().subscriber.size());
					for(MessageQueue mq : curLvl.getData().subscriber) {
						//System.out.println("Sto notificando : "+ curLvl.getData().name);
						mq.send(m);
					}
					
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
	
	
	/*public synchronized void byebye (String threadid) {
		root.getData().subscriber.removeIf(b -> b.getThId().equals(threadid));
		unSub(root, threadid);	
	}*/
	
	private void unPrint(Node<Subject> node, int lvl) {
		if (node.getChildren().isEmpty())
			return;
		
		for (Node<Subject> sub : node.getChildren()) {
			System.out.println("Lvl: " + lvl + " " + sub.getData().name);
			unPrint(sub, lvl+1);
		}
		
	}
	
	
	public synchronized void printTable (){
		
		for(Node<Subject> node : root.getChildren()) {
			System.out.println("Lvl: 1 " + node.getData().name);
			unPrint(node, 2);
		}
		
	}
	
	
	private void visita_rec(Node<Subject> node) {
		if (node.getChildren().isEmpty())
			return;
		
		for (Node<Subject> sub : node.getChildren()) {
				System.out.println(sub.getData().name);
			
			visita_rec(sub);
		}
		
	}
	
	
	public synchronized void visita (Node<Subject> root){
		
		for(Node<Subject> node : root.getChildren()) {
			visita_rec(node);
		}
		
	}
	
}
