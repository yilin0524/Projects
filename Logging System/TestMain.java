package projectone;
/**
 * 
 * 40138516 Jianbin Lai
 * 40085480 Liangzhao Lin
 * 40083064 Yilin Li
 * 40054428 Yanzhen Li 
 *
 */
public class TestMain {

    public static void main(String[] args) {
        Server host = new Server("My Server");
        Client jack = new Client("Jack", "evil.net");
        Client jill = new Client("Jill", "evil.net");
        Client jekyll = new Client("Jekyll", "student.net");
        Client hyde = new Client("Hyde", "evil.net");
        
        jack.connect(host); // accommodate
        jill.connect(host); // accommodate
        jekyll.connect(host); // accommodate
        jekyll.disconnect(host); // accommodate
        
        jack.getAllClients(); // suspicious: Do not accommodate, blacklist and disconnect
        jill.getAllClients(); // already blacklisted; Do not accommodate and disconnect
        jill.getAllClients(); // not accommodated
        jill.disconnect(host); // not accommodated
        
        jill.connect(host); // recorded, but not accommodated
        hyde.connect(host); // recorded, but not accommodated
        
        host.getClients();
    }

}
