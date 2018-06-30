package info.nemoworks.mdfs.master.clientnode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClientManager {

    private List<ClientNode> Clients;

    public ClientManager(){
        Clients = new ArrayList<ClientNode>();
    }

    public void addClientNode(String url){
        ClientNode newClientNode = new ClientNode(url);
        Clients.add(newClientNode);
    }

    public void deleteClientNode(String clientUrl) {
        for(ClientNode clientNode : Clients){
            if(clientNode.getClientUrl().equals(clientUrl)){
                Clients.remove(clientNode);
                break;
            }
        }
    }

    //考虑负载均衡
    public List<String> getValidClientUrl(int replicas){
        Collections.sort(Clients);
        List<String> ClientsUrl = new ArrayList<>();
        int clientCount = 0;
        for(ClientNode i:Clients){
            ClientsUrl.add(i.getClientUrl());
            clientCount += 1;
            if(clientCount == replicas){
                break;
            }
        }
        return ClientsUrl;
    }

    public ClientNode getClientNode(String clientUrl){
        for(ClientNode clientNode : Clients){
            if(clientNode.getClientUrl().equals(clientUrl)){
                return clientNode;
            }
        }
        return null;
    }
}
