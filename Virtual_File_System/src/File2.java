

import java.util.ArrayList;

public class File2 {
    public String fileName;
    public String filePath;
    public ArrayList<Integer> fileAllocatedBlocks = new ArrayList<>();
    public boolean deleted = false;

    public File2()
    {

    }
    public File2(String fileName, String filePath, boolean deleted)
    {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void setName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return this.fileName;
    }

    public void setAllocatedBlocks(ArrayList<Integer> fileAllocatedBlocks) {
        this.fileAllocatedBlocks = fileAllocatedBlocks;
    }

    public ArrayList<Integer> getAllocatedBlocks() {
        return fileAllocatedBlocks;
    }

    @Override
    public String toString() {
        return "name = " + fileName + "\n"+
               "filePath = " + filePath + "\n" +
                "allocatedBlocks = " + fileAllocatedBlocks+"\n" +
                "deleted = " + deleted +"\n";

    }
}