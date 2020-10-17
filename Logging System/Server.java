package projectone;
import java.util.*;
public class Server {
  private String name;
  private boolean active;
  Date date = Calendar.getInstance().getTime();
  private ArrayList<Client> clients = new ArrayList<Client>();
  public Server(String name) {
    this.name = name;
    this.active = true;
  }
  public boolean isActive() {
    return active;
  }

  public void attach (Client client) {
    clients.add(client);
  }
  public void detach (Client client) {
    clients.remove(client);
  }
  public String ping() {
    if (isActive())
      return "PING " + this.toString() + " " + this.date + ".";
    else
      return null;
  }
  public void getClients() {
    System.out.print("Clients: " + clients + "\n");
  }
  public boolean isClient(Client client) {
    return clients.contains(client);
  }
  public String toString() {
    return name;
  }
}


