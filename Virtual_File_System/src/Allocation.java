import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Allocation {

    public ArrayList<Directory> directories = new ArrayList<>();
    public ArrayList<Blocks> freeBlocks = new ArrayList<>();
    public ArrayList<Blocks> allocatedBlocks = new ArrayList<>();
    public String memory;

    public abstract boolean createFile(String filePath, int size);

    public abstract boolean deleteFile(String filePath);

    public boolean createFolder(String folderPath) {
        String [] pathParts = folderPath.split("/");
        boolean exist = PathExist(pathParts);
        // First condition :  The path is already exist
        if (exist){
            // Second condition : No folder with the same name is already created under this path
            int i ;
            for (i = 0; i < directories.size(); i++)
            {
                // get the index of the directory that we want to create the folder in
                if(pathParts[pathParts.length - 2].equals(directories.get(i).getName()))
                {
                    break;
                }
            }
            // check if the folder we want to create exist or not
            for (int j = 0; j < directories.get(i).getSubDir().size() ; j++)
            {
                if(directories.get(i).getSubDir().get(j).getName().equals(pathParts[pathParts.length - 1]))
                {
                    System.out.println("Error: The folder name already exists");
                    return false;
                }
            }
        }
        else
        {
            System.out.println("Error: the path is not exist");
            return false;
        }

        Directory directory= new Directory(pathParts[pathParts.length-1],folderPath,false);

        for (int i = 0; i < directories.size(); i++)
        {
            // get the index of the directory that we want to create the folder in
            if(pathParts[pathParts.length - 2].equals(directories.get(i).getName()))
            {
                directories.get(i).getSubDir().add(directory);
                directory.parentDir = directories.get(i);
                directories.add(directory);

                break;
            }
        }
        System.out.println("The folder is created successfully");

        return true;
    }

    public boolean deleteFolder(String folderPath) {
        String [] pathParts = folderPath.split("/");
        boolean exist = PathExist(pathParts);
        int start = 0;
        if (exist) {
            int i ;
            for (i = 0; i < directories.size(); i++)
            {
                if(pathParts[pathParts.length - 1].equals(directories.get(i).getName())) {
                    break;
                }
            }
            for(int j = 0; j < directories.get(i).files.size(); j++){
                String filePath = directories.get(i).files.get(j).filePath;
                boolean delete = deleteFile(filePath);
                if(!directories.get(i).files.isEmpty()){
                    j--;
                }
            }
            for(int j = 0; j < directories.get(i).subDir.size(); j++){
                String dirPath = directories.get(i).subDir.get(j).directoryPath;
                boolean delete = deleteFolder(dirPath);
                if(!directories.get(i).subDir.isEmpty()){
                    j--;
                }
            }
            if(directories.get(i).subDir.size() == 0){
                int l ;
                for (l = 0; l < directories.size(); l++)
                {
                    if(pathParts[pathParts.length - 2].equals(directories.get(l).getName())) {
                        break;
                    }
                }
                boolean flag = false;
                for(int m = 0; m  < directories.get(l).subDir.size(); m++){
                    if(directories.get(l).subDir.get(m).getName().equals(pathParts[pathParts.length - 1])){
                        flag = true;
                        Directory dir = directories.get(l).subDir.get(m);
                        directories.get(l).subDir.remove(dir);
                        for(int k = 0; k < directories.size(); k++){
                            if(directories.get(k).getName().equals(pathParts[pathParts.length - 1])){
                                directories.remove(directories.get(k));
                            }
                        }
                    }

                }
                if(!flag)
                {
                    System.out.println("Error: the folder does not exist under the path specified");
                    return false;
                }
            }
        }
        else{
            System.out.println("Error: the folder path is not exist");
            return false;
        }
        System.out.println("The folder is deleted successfully");
        return true;
    }

    public void displayDiskStatus() {
        int emptySpace = 0, allocatedSpace = 0;
        for (int i = 0; i < memory.length(); i++) {
            if(memory.charAt(i) == '0')
                emptySpace++;
        }
        for (int i = 0; i < memory.length(); i++) {
            if(memory.charAt(i) == '1')
                allocatedSpace++;
        }
        System.out.println("1- Empty space : " + emptySpace +" KB");
        System.out.println("2- Allocated space : " + allocatedSpace+" KB");
        System.out.println("3- Empty Blocks in the Disk : " );
        System.out.println(freeBlocks);
        System.out.println("4- Allocated Blocks in the Disk : ");
        System.out.println(allocatedBlocks);
    }

    public void displayDiskStructure(Directory root, int level) {
        for(int i = 0; i < level ; i++) {
            System.out.print("\t");
        }
        System.out.println(root.getName());
        if(root.files.size() != 0){
            for (File2 file2 : root.files) {
                for(int l = 0; l < level + 1 ; l++){
                    System.out.print("\t");
                }
                System.out.println(file2.getName());
            }
        }
        if(root.subDir.size() != 0){
            for (Directory directory : root.subDir) {
                displayDiskStructure(directory, level + 1);
            }
        }
    }


    public boolean PathExist(String[] pathParts) {
        for (int part = 0; part < pathParts.length - 1; part++) {
            boolean dirFound = false, childFound = false;
            for (int i = 0; i < directories.size(); i++) {
                if (directories.get(i).getName().equals(pathParts[part])) {
                    dirFound = true;
                    for (int j = 0; j < directories.get(i).getSubDir().size(); j++) {
                        if (directories.get(i).getSubDir().get(j).getName().equals(pathParts[part + 1])) {
                            childFound = true;
                            break;
                        }
                    }
                } else {
                    continue;
                }
                if (childFound == true || part == pathParts.length - 2)
                    break;
                else
                    return false;
            }
            if (dirFound == false) {
                return false;
            }
        }
        return true;
    }

    public abstract void loadFromFile(String fileName) throws IOException;


    public abstract void writeOutputToFile(Directory root, FileWriter myWriter) throws IOException;

    public abstract void writeToFile(Directory root, FileWriter myWriter) throws IOException;

    public void saveToFile(String fileName) throws IOException {
        FileWriter myWriter = new FileWriter(fileName);
        writeToFile(directories.get(0) , myWriter);
        myWriter.write("---" + "\n");
        myWriter.write(memory + "\n");
        myWriter.write("---" + "\n");
        writeOutputToFile(directories.get(0) , myWriter);
        myWriter.write("###\n");
        myWriter.close();
    }


    public void calcAllocatedBlocks() {
        boolean allocatedCheck = false;
        int allocatedStart = 0, allocatedEnd = 0;
        allocatedBlocks = new ArrayList<>();

        for (int i = 0; i < memory.length(); i++) {
            if (memory.charAt(i) == '1' && allocatedCheck == false) {
                allocatedStart = i;
                allocatedCheck = true;
            }
            if ((i + 1) < memory.length() && memory.charAt(i) == '1' && memory.charAt(i + 1) == '0' && allocatedCheck == true) {
                allocatedCheck = false;
                allocatedEnd = i;
                Blocks freeObj = new Blocks(allocatedStart, allocatedEnd);
                allocatedBlocks.add(freeObj);
            }
            if (i == memory.length() - 1 && memory.charAt(i) == '1' && allocatedCheck == true) {
                allocatedCheck = false;
                allocatedEnd = i;
                Blocks freeObj = new Blocks(allocatedStart, allocatedEnd);
                allocatedBlocks.add(freeObj);
            }
        }
    }

    public void calcFreeBlocks() {
        int freeStart = 0, freeEnd = 0;
        boolean freeCheck = false;
        freeBlocks = new ArrayList<>();
        for (int i = 0; i < memory.length(); i++) {
            if (memory.charAt(i) == '0' && freeCheck == false) {
                freeStart = i;
                freeCheck = true;
            }
            if ((i + 1) < memory.length() && memory.charAt(i) == '0' && memory.charAt(i + 1) == '1' && freeCheck == true) {
                freeCheck = false;
                freeEnd = i;
                Blocks freeObj = new Blocks(freeStart, freeEnd);
                freeBlocks.add(freeObj);
            }
            if (i == memory.length() - 1 && memory.charAt(i) == '0' && freeCheck == true) {
                freeCheck = false;
                freeEnd = i;
                Blocks freeObj = new Blocks(freeStart, freeEnd);
                freeBlocks.add(freeObj);
            }
        }
    }


}
