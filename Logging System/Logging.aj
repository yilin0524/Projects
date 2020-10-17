package projectone;
import java.util.ArrayList;

public privileged aspect Logging {

    pointcut pct_client_connect(Client client, Server server):execution(void Client.connect(Server)) && target(client) && args(server);
    pointcut pct_server_attach(Server server, Client client):execution(void Server.attach(Client)) && target(server) && args(client);
    pointcut pct_client_disconnect(Client client, Server server):execution(void Client.disconnect(Server)) && target(client) && args(server);


    before(Client client, Server server):    pct_client_connect(client,server){
        System.out.println("CONNECTION REQUEST >>> " + client + " requests connection to " + server + ".\r\n");
    }

    after(Server server, Client client):    pct_server_attach(server, client){
        System.out.println("Connection established between " + client + " and " + server + ".\r\n"
                + "Clients logged in: " + server.clients + "\r\n\r\n");
    }

    after(Client client, Server server): 	pct_client_disconnect(client,server){
        System.out.println("Connection broken between "+client+" and "+server+".\n"
                +"Clients logged in: "+server.clients+"\r\n\r\n");
    }
}