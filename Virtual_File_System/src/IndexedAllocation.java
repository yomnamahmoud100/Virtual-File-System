import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


public class IndexedAllocation extends Allocation{

    public HashMap<Integer , ArrayList<Integer>> table = new HashMap<>(500);

    public IndexedAllocation() {}

    @Override
    public boolean createFile(String filePath, int size) {
        String [] pathParts = filePath.split("/");

        boolean exist = PathExist(pathParts);

        // First condition :  The path is already exist
        if (exist){
            // Second condition : No file with the same name is already created under this path
            int i ;
            for (i = 0; i < directories.size(); i++)
            {
                // get the index of the directory that we want to create the file in
                if(pathParts[pathParts.length - 2].equals(directories.get(i).getName()))
                {
                    break;
                }
            }
            // check if the file we want to create exist or not
            for (int j = 0; j < directories.get(i).getFiles().size() ; j++)
            {
                if(directories.get(i).getFiles().get(j).getName().equals(pathParts[pathParts.length - 1]))
                {
                    System.out.println("Error: The file name already exists");
                    return false;
                }
            }

            File2 file = new File2(pathParts[pathParts.length - 1], filePath,false);

            // Third condition :  Enough space exists

            //allocate blocks from memory to the file
            int size2 = size;
            char [] newMemory = memory.toCharArray();
            for (int j = 0; j < size && size2 >= 0; j++) {
                int random = generateRandomBlock();
                newMemory[random] ='1';
                file.fileAllocatedBlocks.add(random);
                size2--;
            }

            Collections.sort(file.fileAllocatedBlocks);

            if (size2 != 0)
            {
                System.out.println("Error: there is no enough space");
                return false;
            }

            memory = String.valueOf(newMemory);
            calcAllocatedBlocks();
            calcFreeBlocks();

            for (int k = 0; k < directories.size(); k++)
            {
                // get parent
                if(pathParts[pathParts.length - 2].equals(directories.get(k).getName()))
                {
                    directories.get(k).files.add(file);
                    break;
                }
            }
        }
        else
        {
            System.out.println("Error: the path is not exist");
            return false;
        }
        System.out.println("The file is created successfully");
        return true;
    }

    @Override
    public boolean deleteFile(String filePath) {
        String [] pathParts = filePath.split("/");
        boolean exist = PathExist(pathParts);
        if(exist){
            int i ;
            for (i = 0; i < directories.size(); i++)
            {
                // get the index of the directory that we want to delete the file in
                if(pathParts[pathParts.length - 2].equals(directories.get(i).getName()))
                {
                    break;
                }
            }
            // check if the file we want to delete exist or not
            boolean flag = false;
            for (int j = 0; j < directories.get(i).getFiles().size() ; j++)
            {
                if(directories.get(i).getFiles().get(j).getName().equals(pathParts[pathParts.length - 1])) {
                    flag = true;
                    File2 obj = directories.get(i).getFiles().get(j);
                    obj.deleted = true;

                    //deallocate the memory
                    char[] newMemory = memory.toCharArray();
                    if (obj.fileAllocatedBlocks.size() != 0){
                        for (int k = 0; k < obj.fileAllocatedBlocks.size(); k++) {
                            newMemory[obj.fileAllocatedBlocks.get(k)] = '0';
                        }
                    }
                    memory = String.valueOf(newMemory);

                    //remove the file from indices table
                    table.remove(obj.fileAllocatedBlocks.get(0));

                    //remove the file from its parent directory and calculate free and allocated blocks
                    directories.get(i).getFiles().remove(obj);
                    calcAllocatedBlocks();
                    calcFreeBlocks();
                    break;
                }
            }
            if(!flag)
            {
                System.out.println("Error: the file does not exist under the path specified");
                return false;
            }
        }
        else{
            System.out.println("Error: the file path is not exist");
            return false;
        }
        System.out.println("The file is deleted successfully");
        return true;
    }


    @Override
    public void loadFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file)));
        String line;
        int memoryCheck = 0;

        //loading  .vfs file
        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == 'd') {
                // delete d and space from line and return it without them
                String sub1 = line.substring(2);
                String[] subs = sub1.split("/"); // root  nice   rock
                Directory dir = new Directory(subs[subs.length - 1], sub1, false);
                if (subs.length >= 2) {
                    String parent = subs[subs.length - 2];

                    for (int i = 0; i < directories.size(); i++) {

                        if (directories.get(i).getName().equals(parent)) {
                            directories.get(i).subDir.add(dir);
                            dir.parentDir = directories.get(i);
                            break;

                        }
                    }
                }
                directories.add(dir);

            }
            else if (line.charAt(0) == 'f') {
                String sub1 = line.substring(2);
                String[] subs = sub1.split("/");
                String nameAndAllocation = subs[subs.length - 1];
                String indices = "";
                String name = "";
                String path = "";
                int index;
                int m;
                for (m = 0; m < nameAndAllocation.length(); m++) {
                    if (nameAndAllocation.charAt(m) != '<') {
                        name += nameAndAllocation.charAt(m);
                    }
                    else break;
                }

                for (int i = 0; i < subs.length - 1; i++) {
                    path += subs[i] + "/";
                }

                path += name;
                File2 file1 = new File2(name, path, false);

                indices = nameAndAllocation.substring(m + 1, nameAndAllocation.length() - 1); //skipping < and >
                String[] indicesArray = indices.split(",");

                //add allocated blocks to the file
                for(int i = 0; i < indicesArray.length; i++)
                {
                    index = Integer.parseInt(indicesArray[i]);
                    file1.fileAllocatedBlocks.add(index);
                }

                //store the allocated list to the indices table
                Collections.sort(file1.fileAllocatedBlocks);
                table.put(file1.fileAllocatedBlocks.get(0), file1.fileAllocatedBlocks);

                //assign the file to its parent directory
                String parent = subs[subs.length - 2];
                for (int i = 0; i < directories.size(); i++) {
                    if (directories.get(i).getName().equals(parent)) {
                        directories.get(i).files.add(file1);
                        break;
                    }
                }
            }
            else if (line.equals("---") && memoryCheck == 0) {
                memoryCheck = 1;
                continue;

            }
            //store the memory
            else if(memoryCheck == 1 )
            {
                memory = line;
                calcAllocatedBlocks();
                calcFreeBlocks();
                break;
            }
        }
    }


    @Override
    public void writeOutputToFile(Directory root, FileWriter myWriter) throws IOException {
        if(root.files.size() != 0){
            for (File2 file2 : root.files) {
                int size =  file2.getAllocatedBlocks().size();
                if(size != 0) {
                    int start = file2.getAllocatedBlocks().get(0);
                    myWriter.write("\n");
                    myWriter.write(file2.filePath + "  " + start + "\n");
                    myWriter.write(start + "    ");
                    int i ;
                    for (i = 1; i < file2.fileAllocatedBlocks.size(); i++){
                        myWriter.write(file2.getAllocatedBlocks().get(i) + " " );
                    }
                    myWriter.write("\n");
                }
            }
        }
        if(root.subDir.size() != 0){
            for (Directory directory : root.subDir) {
                writeOutputToFile(directory , myWriter);
            }
        }
    }

    @Override
    public void writeToFile(Directory root, FileWriter myWriter) throws IOException {
        myWriter.write("d " + root.directoryPath + "\n");
        if(root.files.size() != 0){
            for (File2 file2 : root.files) {
                int size =  file2.getAllocatedBlocks().size();
                if(size != 0) {
                    int end = file2.getAllocatedBlocks().get(size - 1);
                    myWriter.write("f " + file2.filePath + "<" );
                    for(int i = 0 ; i < file2.getAllocatedBlocks().size() - 1 ; i++)
                        myWriter.write( file2.getAllocatedBlocks().get(i)+ "," );
                    myWriter.write(end + ">" + "\n");
                }
            }
        }
        if(root.subDir.size() != 0){
            for (Directory directory : root.subDir) {
                writeToFile(directory , myWriter);
            }
        }
    }


    //generate random blocks to allocate the file in
    public int generateRandomBlock(){
        Random rand = new Random();
        int upperbound = 500;
        int random = rand.nextInt(upperbound);
        //must be empty
        while(memory.charAt(random) == '1'){
            random = rand.nextInt(upperbound);
        }
        return random;
    }


}
