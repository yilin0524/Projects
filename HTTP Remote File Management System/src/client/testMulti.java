package client;

public class testMulti {
	public static void main(String[] args) {
		//one read, one read
//		CURLCommand c1 = new CURLCommand("httpc get -v -h Content-Type:html/text -h Content-Disposition:attachment;filename=\"c1.txt\" http://132.205.93.45:8081/aaa.txt");
//		CURLCommand c2 = new CURLCommand("httpc get -v -h Content-Type:html/text -h Content-Disposition:attachment;filename=\"c2.txt\"  http://132.205.93.45:8081/aaa.txt");
		
		//one write, one write
//		CURLCommand c1 = new CURLCommand("httpc post -v -h Content-Type:application/json -d client1 http://132.205.93.45:8081/aaa.txt");
//		CURLCommand c2 = new CURLCommand("httpc post -v -h Content-Type:text/html -d client2 http://132.205.93.45:8081/aaa.txt");

		//c1 read, c0 c2 write
		Client c0 = new Client("httpc post -v -h Content-Type:text/html -d client000write http://127.0.0.1:8081/a.txt");
		Client c1 = new Client("httpc get -v http://127.0.0.1:8081/a.txt");
		Client c2 = new Client("httpc post -v -h Content-Type:text/html -d client222write http://127.0.0.1:8081/a.txt");

		Thread t0 = new Thread(c0);
		Thread t1 = new Thread(c1);
		Thread t2 = new Thread(c2);
		
		t0.start();
		t1.start();
		t2.start();
	}

}
