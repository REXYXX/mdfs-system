package info.nemoworks.mdfs.client.block;

import info.nemoworks.mdfs.client.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class BlockManager {
    @Autowired
    private StorageService storageService;

    @PostMapping("/")
    public String handleFile(@RequestParam("block") MultipartFile file) throws IOException {
        storageService.store(file);
        return "upload success";
    }
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> downloadBlock(@PathVariable("filename") String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
    @DeleteMapping("/{filename}")
    public void handleFile(@PathVariable("filename") String filename) throws IOException {
        storageService.delete(filename);
    }
}
