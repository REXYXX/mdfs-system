package info.nemoworks.mdfs.master.masternode;

import info.nemoworks.mdfs.master.block.Block;
import info.nemoworks.mdfs.master.block.BlockManager;
import info.nemoworks.mdfs.master.clientnode.ClientManager;
import info.nemoworks.mdfs.master.filesystem.FileCase;
import info.nemoworks.mdfs.master.filesystem.FileSystem;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@ConfigurationProperties(prefix="block")
public class MasterNode {

    private int BLOCK_SIZE;

    private int BLOCK_REPLICAS;

    private String Download_DIRECTORY = "Download/";

    private ClientManager clientManager = new ClientManager();

    private FileSystem fileSystem = new FileSystem();

    private BlockManager blockManager = new BlockManager();

    public MasterNode(){

        File file = new File("Blocks");
        FileSystemUtils.deleteRecursively(file);
        file.mkdir();

        file = new File("Download");
        FileSystemUtils.deleteRecursively(file);
        file.mkdir();
    }

    public void setBLOCKSIZE(int blockSize){
        BLOCK_SIZE = blockSize;
    }

    public void setBLOCKREPLICAS(int blockreReplicas) {
        BLOCK_REPLICAS = blockreReplicas;
    }

    public void loadBalanceWhenDeleteClient(String clientUrl) throws IOException {
        List<FileCase> files = fileSystem.getFiles();
        for(FileCase file :files){
            for(Block block : file.getBlocks()){
                boolean blockInClient = false;
                for(String oldClientUrl: block.getClientsUrl()) {
                    if (oldClientUrl.equals(clientUrl)) {
                        blockInClient = true;
                        block.removeClientUrl(clientUrl);
                        break;
                    }
                }
                System.out.println("1:" + " "+ block.getBlockId()+" "+blockInClient + " " +  block.getClientsUrl().size());
                if(blockInClient && block.getClientsUrl().size()!=0){
                    List<String> storageClientsUrl = clientManager.getValidClientUrl(BLOCK_REPLICAS+1);
//                        System.out.println("new:  " + storageClientsUrl.get(0));
                    String storageClientUrl = null;
                    for(String copy : storageClientsUrl){
                        boolean blockInstorageClient = false;
                        for(String oldClientUrl : block.getClientsUrl()){
                            if(oldClientUrl.equals(copy) || copy.equals(clientUrl)){
                                blockInstorageClient = true;
                                break;
                            }
                        }
                        if(!blockInstorageClient){
                            storageClientUrl = copy;
                            break;
                        }
                    }
                    System.out.println("2:" + storageClientUrl);
                    if(storageClientUrl != null) {
                        blockManager.downloadBlockFromClient(block.getClientsUrl().get(0), block.getBlockId());
                        blockManager.uploadBlockToDataNode(block.getBlockId(), storageClientUrl);
                        blockManager.deleteClientBlock(clientUrl, block.getBlockId());
                        blockManager.deleteServerBlock(block.getBlockId());
                        clientManager.getClientNode(storageClientUrl).addBlocks(1);
                        block.addClientUrl(storageClientsUrl.get(0));
                    }
                }
            }
        }
        clientManager.deleteClientNode(clientUrl);
    }

    public void addClientNode(String url){
        clientManager.addClientNode(url);
    }

    public void deleteClientNode(String clientUrl) throws IOException {
        loadBalanceWhenDeleteClient(clientUrl);
    }

    public Boolean uploadFile(MultipartFile file, String fileUrl) throws IOException {
        System.out.println("uploading file ...");
        if(fileSystem.isFileExit(fileUrl)){
            System.out.println("file exits");
            System.out.println("uploading fails");
            return false;
        }
        // 分块
        int numBytes = file.getBytes().length;
        int numBlocks = numBytes / BLOCK_SIZE;
        numBlocks = (numBytes) % this.BLOCK_SIZE == 0 ? numBlocks : numBlocks + 1;

        FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
        BufferedInputStream buff = new BufferedInputStream(fileInputStream );
        // 存储blocks信息
        List<Block> storageBlocks = new ArrayList<Block>();

        for(int i=0; i<numBlocks; i++){
            byte blockByteList[] = new byte[BLOCK_SIZE];
            int blocksize = buff.read(blockByteList);
            // block id
            String blockid = file.getOriginalFilename().replace(".","")+ i;
            // 储存在server硬盘
            blockManager.store(blockByteList, blockid, blocksize);
            List<String> ClientsUrl = clientManager.getValidClientUrl(BLOCK_REPLICAS);
            //上传到client节点
            for(int copy_index=0; copy_index<BLOCK_REPLICAS; copy_index++){
                blockManager.uploadBlockToDataNode(blockid, ClientsUrl.get(copy_index));
                clientManager.getClientNode(ClientsUrl.get(copy_index)).addBlocks(1);
            }
            storageBlocks.add(new Block(blockid,ClientsUrl,blocksize));
            //删除server端保存的文件
            blockManager.deleteServerBlock(blockid);
        }
        fileInputStream.close();
        buff.close();

        //将文件添加到文件系统中
        FileCase newfile = new FileCase(fileUrl, file.getOriginalFilename(),storageBlocks,numBytes);
        fileSystem.addFile(newfile);

        System.out.println("uploading success");
        return true;
    }

    public Resource downloadFile(String fileurl) throws IOException {

        if(!fileSystem.isFileExit(fileurl)){
            System.out.println("file not exits");
            return null;
        }
        // 获取文件信息
        FileCase file = fileSystem.getFileByUrl(fileurl);
        String filename = file.getFileName();

        // 创建新文件，用作拼接Block
        File downloadFile = new File(Download_DIRECTORY + filename);
        downloadFile.createNewFile();

        // 文件拼接
        FileChannel outChannel = new FileOutputStream(downloadFile).getChannel();

        // 获取该文件有关的blocks信息
        List<Block> blocks = file.getBlocks();

        int numBlocks = blocks.size();
        for(Block block : blocks){
            // 获取该block的一个备份
            String clientUrl = block.getClientsUrl().get(0);
            String blockId = block.getBlockId();
            long blockSize = block.getSize();
            File blockFile = null;
            // 从url下载Block, 暂时存到server Download目录
            blockFile = blockManager.downloadBlockFromClient(clientUrl, blockId);
            // 把该block的内容拼接到文件downloadFile末尾
            FileChannel inChannel = new FileInputStream(blockFile).getChannel();
            inChannel.transferTo(0, blockSize, outChannel);
            inChannel.close();
            //删除sever上磁盘的信息
            blockManager.deleteServerBlock(blockId);
        }
        outChannel.close();

        Path path = Paths.get(Download_DIRECTORY + filename);
        Resource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        return null;
    }

    public Boolean deleteFile(String fileurl)throws IOException{
        if(!fileSystem.isFileExit(fileurl)){
            System.out.println("file not exits");
            return false;
        }
        // 获取文件基本信息
        FileCase file = fileSystem.getFileByUrl(fileurl);

        // 获取该文件有关的blocks信息
        List<Block> blocks = file.getBlocks();
        for(Block block : blocks){
            // 获取该block所在的所有client的url
            List<String> clientsUrl = block.getClientsUrl();
            String blockId = block.getBlockId();
            for(String clientUrl: clientsUrl){
                blockManager.deleteClientBlock(clientUrl, blockId);
                clientManager.getClientNode(clientUrl).delBlocks(1);
            }
        }
        // 从文件系统中删除
        fileSystem.deleteFile(fileurl);
        System.out.println("delete file success");
        return true;
    }
}
