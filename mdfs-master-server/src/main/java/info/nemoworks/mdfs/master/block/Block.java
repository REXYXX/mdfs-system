package info.nemoworks.mdfs.master.block;

import java.util.List;

public class Block {
    private String blockId;
    private List<String> clientsUrl;
    private long size;

    public Block(String blockid, List<String> clientsurl, long blocksize){
        blockId = blockid;
        clientsUrl = clientsurl;
        size = blocksize;
    }

    public void removeClientUrl(String clientUrl){
        clientsUrl.remove(clientUrl);
    }
    public void addClientUrl(String clientUrl){
        clientsUrl.add(clientUrl);
    }

    public List<String> getClientsUrl() {
        return clientsUrl;
    }

    public long getSize() {
        return size;
    }

    public String getBlockId() {
        return blockId;
    }
}
