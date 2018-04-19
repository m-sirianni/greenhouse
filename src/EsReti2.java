public class EsReti2 {

	public static void main(String[] args) throws InterruptedException {
		MessageQueue mq = new MessageQueue();
		SubjectTable st = new SubjectTable();
		
		Runnable run_send = new SendRunnable(mq);
		Runnable run_rcv = new ReceiveRunnable(mq);
		
		for (int i = 0; i < 3; i++)
			new Thread(run_send).start();
		
		for (int i = 0; i < 1; i++)
			new Thread(run_rcv).start();
		
		while(true){
			System.out.println(mq.Size());
		}
		
		
		Runnable run_sub = new SubRunnable(mq,st);
		Node<Subject> root;
		Node<Subject> ris = null;
		int n = 3;
		
		Thread[] th = new Thread[n];
		
		for(int i = 0; i < n; i++) {
			th[i] = new Thread(run_sub);
			th[i].start();
		}
		
		for(int i = 0; i < n; i++) th[i].join();
		
		root= st.getRoot();
		st.visita(root);
		st.byebye(root, "main");
		System.out.println("////////=====================/////////////");
		st.visita(root);
		//ris = st.lastNode(root, "root/home/nodes/n1", mq, 0);
		//System.out.println(Arrays.toString(ris.getData().subscriber.get(0).getQueue().toArray()));
		//Node<Subject> ris2 = null;
		//ris2 = st.lastNode(root, "root/home/nodes/n1", mq, 0);
		//System.out.println(ris2.getData().subscriber.get(0).getQueue().get(0).toString());
		
		//st.printTable(mq.getThId());
		
		////////////////
		MessageQueue mq1 = new MessageQueue();
		MessageQueue mq2 = new MessageQueue();
		MessageQueue mq3 = new MessageQueue();
		
		SubjectTable st = new SubjectTable();
		
		
		
		
	}
	

}
