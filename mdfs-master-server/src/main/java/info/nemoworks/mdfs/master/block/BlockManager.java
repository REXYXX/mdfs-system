package info.nemoworks.mdfs.master.block;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BlockManager {

    private final String BLOCKS_DIRECTORY = "Blocks/";

    public void store(byte[] blockByteArray, String blockId, int blockSize) throws IOException {
        String filePath = BLOCKS_DIRECTORY + blockId;
        File blockFile = new File(filePath);
        FileOutputStream out = new FileOutputStream(blockFile);
        out.write(blockByteArray, 0, blockSize);
        out.close();
    }

    public void uploadBlockToDataNode(String blockId, String clientUrl) {
        String filePath = BLOCKS_DIRECTORY + blockId;

        // param参数
        FileSystemResource resource = new FileSystemResource(new File(filePath));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("block", resource);

        // 发起Post请求
        RestTemplate rest = new RestTemplate();

        //上传
        rest.postForObject(clientUrl, param, String.class);
    }

    public File downloadBlockFromClient(String clientUrl, String blockId) throws IOException {
        // Post请求的完整url
        String url = clientUrl + "/" + blockId;

        //获取Block文件
        UrlResource urlResource = new UrlResource(new URL(url));
        InputStream inputStream = urlResource.getInputStream();

        // 持久化到硬盘
        Path blockPath = Paths.get(BLOCKS_DIRECTORY + blockId);
        Files.copy(inputStream,blockPath, StandardCopyOption.REPLACE_EXISTING);

        return new File(BLOCKS_DIRECTORY + blockId);
    }

    public void deleteClientBlock(String clientUrl, String blockId) {
        String url = clientUrl + "/" + blockId;
        RestTemplate rest = new RestTemplate();
        rest.delete(url);
    }

    //删除server端的blocks
    public void deleteServerBlock(String blockId) {
        File file = new File(BLOCKS_DIRECTORY + blockId);
        if(file.exists()){
            file.delete();
        }
    }

}
