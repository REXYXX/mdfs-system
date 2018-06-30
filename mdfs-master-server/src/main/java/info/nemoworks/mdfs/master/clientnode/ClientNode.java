package info.nemoworks.mdfs.master.clientnode;

public class ClientNode implements Comparable<ClientNode>{

    private String clientUrl;

    private int blockNum = 0;

    @Override
    public int compareTo(ClientNode clientNode){
        if(this.blockNum > clientNode.blockNum) {
            return 1;
        }
        else {
            return -1;
        }
    }

    public ClientNode(String url){
        clientUrl = url;
    }

    public int getBlockNum(){
        return blockNum;
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public void addBlocks(int blocknum){
        blockNum += blocknum;
    }

    public void delBlocks(int blocknum){
        blockNum -= blocknum;
    }
}
