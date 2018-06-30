package info.nemoworks.mdfs.master.masternode;

import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class MasterController {

    @Autowired
    private MasterNode masterNode = new MasterNode();


    @PostMapping("/**")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        String fileUrl = request.getRequestURI();
        fileUrl = URLDecoder.decode(fileUrl, "UTF-8");
        masterNode.uploadFile(file, fileUrl);
        return "upload";
    }

    @GetMapping("/**")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) throws IOException {
        String fileUrl = request.getServletPath();
        Resource file = masterNode.downloadFile(fileUrl);
        // 如果不存在
        if(file == null){
            return null;
        }
        // 返回文件
        HttpHeaders headers = new HttpHeaders();
        Path path = Paths.get(fileUrl);
        String filename = path.getFileName().toString();
        filename = new String(filename.getBytes("UTF-8"), "iso-8859-1");
        headers.add("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("multipart/form-data")).body(file);
    }

    @DeleteMapping("/**")
    @ResponseBody
    public String deleteFile(HttpServletRequest request)throws IOException{
        String fileUrl = request.getServletPath();
        // 如果不存在
        if(masterNode.deleteFile(fileUrl))
            return "delete success";
        else
            return "delete fails";
    }

    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event) {
        InstanceInfo instanceInfo = event.getInstanceInfo();
        String ClientUrl = instanceInfo.getHomePageUrl();
        System.err.println(ClientUrl + "进行注册");
        masterNode.addClientNode(ClientUrl);
    }

    @EventListener
    public void listen(EurekaInstanceCanceledEvent event) throws IOException {
        String clientUrl = "http://" + event.getServerId() + "/";
        System.out.println(clientUrl + "删除节点");
        masterNode.deleteClientNode(clientUrl);
        System.err.println(clientUrl + " 服务下线");
    }
}
