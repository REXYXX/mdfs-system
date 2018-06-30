package info.nemoworks.mdfs.master.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {
    private List<FileCase> Files;

    public FileSystem(){
        Files = new ArrayList<FileCase>();
    }

    public void addFile(FileCase file){
        Files.add(file);
    }

    public void deleteFile(String fileUrl){
        for(FileCase file:Files){
            if(file.getFileUrl().equals(fileUrl)){
                Files.remove(file);
                break;
            }
        }
    }

    public Boolean isFileExit(String fileurl){
        for(FileCase i : Files){
            if(i.getFileUrl().equals(fileurl)){
                return true;
            }
        }
        return false;
    }

    public FileCase getFileByUrl(String fileurl){
        for(FileCase file : Files){
            if(file.getFileUrl().equals(fileurl)){
                return file;
            }
        }
        return null;
    }

    public List<FileCase> getFiles() {
        return Files;
    }
}
