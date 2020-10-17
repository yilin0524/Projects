package projectone;
import java.util.ArrayList;

public privileged aspect Authentication {
    declare precedence: Authentication, Logging;

    pointcut pct_client_server_getClient(Client client, Server server) : call(void Server.getClients()) && this(client)&&target(server);

//   pointcut pct_client_send_message capture any message send by clent.
    pointcut pct_client_send_message(Client client, Server server) : call( * *.*(..))&& this(client)&&target(server);


    //Blacklist is used to record black address
    private ArrayList<String> Server.blackList = new ArrayList();

    //This advice is using to check client's address in blacklist or not when client send any message to server.
    Object around(Client client, Server server):  pct_client_send_message(client,server){
        boolean alreadyinBkList = server.blackList.contains(client.getAddress());
        if(alreadyinBkList==false){
            return proceed(client,server);
        }else {
        	//this client address is already in blacklist
        	//the rest clients with same address will be detached when it try to send a request to the server.
            if (server.isClient(client)){
                server.detach(client);
            }
            return null;
        }
    }

//    Capturing suspicious calls
    void around(Client client, Server server):	pct_client_server_getClient(client,server){
            boolean alreadyinBkList = server.blackList.contains(client.getAddress());
            if(alreadyinBkList==false) {
                System.out.println("WARNING >>> Suspicious call from "+client.getAddress()+": "+thisJoinPoint.toShortString()+"\r\n");
                //add to blacklist
                server.blackList.add(client.getAddress());
                client.disconnect(server);
            }
            //else already in blacklist:do nothing
    }
}
