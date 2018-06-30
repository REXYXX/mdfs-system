package info.nemoworks.mdfs.master.filesystem;

import info.nemoworks.mdfs.master.block.Block;

import java.util.ArrayList;
import java.util.List;

public class FileCase {
    private String fileName;

    private String fileUrl;

    private int bytes;

    private List<Block> blocks = new ArrayList<Block>();

    public FileCase(String fileurl, String filename,List<Block> blockList,  int numbytes){
        fileUrl = fileurl;
        fileName = filename;
        blocks = blockList;
        bytes = numbytes;
    }

    public String getFileName() {
        return fileName;
    }

    public int getBytes() {
        return bytes;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
