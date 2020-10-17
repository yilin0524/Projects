package projectone;
public class Client {
  private String name;
  private String address;
  private Server server;
  public Client(String name, String address) {
    this.name = name;
    this.address = address;
  }
  public void connect(Server server) {
    this.server = server;
    server.attach(this);
  }

  public void disconnect(Server server) {
    server.detach(this);
  }
  public String getAddress() {
    return this.address;
  }
  public String getServer() {
    return this.server.toString();
  }
  public void getAllClients(){
    this.server.getClients();
  }
  public String pingServer() {
    return server.ping();
  }
  public String toString() {
    return name + "@" + address;
  }
}
