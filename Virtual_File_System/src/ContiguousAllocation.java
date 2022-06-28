

import java.io.*;
import java.util.ArrayList;

import java.util.Collections;

public class ContiguousAllocation extends Allocation {

    public ContiguousAllocation() {
    }

    @Override
    public boolean createFile(String filePath , int size) {
        String [] pathParts = filePath.split("/");
        boolean exist = PathExist(pathParts);
        int start = 0;
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

            // Third condition :  Enough space exists
            Collections.sort(freeBlocks,new Compare());
            int check = -1;

            for (int j = 0; j < freeBlocks.size(); j++) {
                if (freeBlocks.get(j).size >= size)
                {
                    //allocate blocks from memory to the file
                    start = freeBlocks.get(j).start;
                    char [] newMemory = memory.toCharArray();
                    for (int k = start; k < (start + size); k++)
                    {
                        newMemory[k] ='1';
                    }
                    memory = String.valueOf(newMemory);

                    calcAllocatedBlocks();
                    calcFreeBlocks();

                    check = 1;
                    break;
                }
            }
            if (check == -1)
            {
                System.out.println("Error: there is no enough space");
                return false;
            }
            File2 file = new File2(pathParts[pathParts.length - 1], filePath,false);
            for (int k = start; k < start + size; k++)
            {
                file.fileAllocatedBlocks.add(k);
            }

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
            System.out.println("Error: the file path is not exist");
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
        // Creating an object of BufferedReader class
        BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file)));
        // Declaring a string variable for storing a line from the file
        String st;

        ArrayList<File2> files;

        //loading  .vfs file
        while ((st = br.readLine()) != null) {
            if (st.charAt(0) == 'd') {
                // delete d and space from st and return it without them
                String sub1 = st.substring(2);
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
            else if (st.charAt(0) == 'f') {
                String sub1 = st.substring(2);
                String[] subs = sub1.split("/");
                String nameAndSize = subs[subs.length - 1];
                String sizeAndIndex = "";
                String name = "";
                String path = "";
                int index, size;
                int m;
                for (m = 0; m < nameAndSize.length(); m++) {
                    if (nameAndSize.charAt(m) != '<') {
                        name += nameAndSize.charAt(m);
                    } else
                        break;
                }
                sizeAndIndex = nameAndSize.substring(m + 1, nameAndSize.length() - 1);
                String[] sizeAndIndexArr = sizeAndIndex.split(",");
                size = Integer.parseInt(sizeAndIndexArr[1]);
                index = Integer.parseInt(sizeAndIndexArr[0]);

                for (int i = 0; i < subs.length - 1; i++) {
                    path += subs[i] + "/";
                }
                path += name;
                File2 file1 = new File2(name, path, false);
                for (int k = index; k < index + size; k++) {
                    file1.fileAllocatedBlocks.add(k);
                }
                //add allocated blocks to the file
                String parent = subs[subs.length - 2];
                for (int i = 0; i < directories.size(); i++) {
                    if (directories.get(i).getName().equals(parent)) {
                        directories.get(i).files.add(file1);
                        break;
                    }
                }

            }
            //store the memory
            else if (st.charAt(0) == '0' || st.charAt(0) == '1') {
                memory = st;
                calcAllocatedBlocks();
                calcFreeBlocks();
            }

        }
    }

    @Override
    public void writeOutputToFile (Directory root , FileWriter myWriter) throws IOException {
        if(root.files.size() != 0){
            for (File2 file2 : root.files) {
                int size =  file2.getAllocatedBlocks().size();
                if(size != 0) {
                    int start = file2.getAllocatedBlocks().get(0);
                    myWriter.write(file2.filePath + " " + start + " " + size + "\n");
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
    public void writeToFile(Directory root ,FileWriter myWriter ) throws IOException {
        myWriter.write("d " + root.directoryPath + "\n");
        if(root.files.size() != 0){
            for (File2 file2 : root.files) {
                int size =  file2.getAllocatedBlocks().size();
                if(size != 0) {
                    int start = file2.getAllocatedBlocks().get(0);
                    myWriter.write("f " + file2.filePath + "<" + start + "," + size +">" + "\n");
                }
            }
        }
        if(root.subDir.size() != 0){
            for (Directory directory : root.subDir) {
                writeToFile(directory , myWriter);
            }
        }
    }

}
