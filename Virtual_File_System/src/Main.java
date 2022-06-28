import javafx.util.Pair;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Main {
    public static HashMap<String , String> users = new HashMap<>();

    public static HashMap <String , ArrayList<Pair<String , String>>> capabilities = new HashMap<>();

    public static String currentUser = "admin";

    public static ArrayList<Directory> directories = new ArrayList<>();

    public static void readUsers() throws IOException
    {
        File file = new File("user.txt");
        BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file)));

        String line;
        while ((line = br.readLine()) != null && line.length() > 0) {
            String[] nameAndPass = line.split(",");
            users.put(nameAndPass[0] , nameAndPass[1]);
        }
    }

    public static void readCapabilities() throws IOException
    {
        File file = new File("capabilities.txt");
        BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file)));
        String line;
        while ((line = br.readLine()) != null && line.length() > 0) {
            String[] str = line.split(",");

            ArrayList<Pair<String , String>> pairs = new ArrayList<>();
            for (int i = 1; i < str.length; i+=2)
            {
                Pair <String, String> pair = new Pair<>(str[i] , str[i+1]);
                pairs.add(pair);
            }
            capabilities.put(str[0] , pairs);
        }
    }

    public static void login(String userName , String password)
    {
        if(users.containsKey(userName))
        {
            if(users.get(userName).equals(password))
            {
                currentUser = userName;
                System.out.println("Logged in successfully");
            }
            else
            {
                System.out.println("The password is not correct");
            }
        }
        else
        {
            System.out.println("The user name does not exist");
        }
    }

    public static String getCurrentUser()
    {
        return currentUser;
    }

    public static void createUser(String userName , String password)
    {
        if(currentUser.equals("admin"))
        {
            if(!users.containsKey(userName))
            {
                users.put(userName , password);
                System.out.println("User Created Successfully!");
            }
            else
            {
                System.out.println("The user name already exists");
            }
        }
        else
        {
            System.out.println("You are not allowed to create a user");
        }
    }

    public static String getCapabilities(String path)
    {
        String  result = "00";
        ArrayList<Pair<String , String>> arrayList =  capabilities.get(path);
        if(arrayList!=null) {
            for (Pair pair : arrayList) {

                if (pair.getKey().equals(currentUser)) {
                    result = (String) pair.getValue();
                    break;
                }
            }
        }
        return result;
    }

    public static String getFolderPath(String filePath)
    {
        String result = "";
        String[] arr = filePath.split("/");
        if(arr.length == 1)
        {
            return "";
        }
        for (int i = 0; i < arr.length - 2; i++)
        {
            result += arr[i] + "/" ;
        }
        result += arr[arr.length - 2];

        return result;
    }
    public static void grant(String userName, String path, String access)
    {
        if (users.containsKey(userName))
        {
            boolean check = false;
            for (int i = 0 ; i < directories.size() ; i++)
            {
                if (directories.get(i).directoryPath.equals(path))
                {
                    check = true;
                    break;
                }
            }
            if (check)
            {
                Pair<String , String> pair = new Pair<>(userName,access);
                if(capabilities.containsKey(path)){
                    boolean check2 = false;
                    for (int i=0; i<capabilities.get(path).size(); i++){
                        // if we have the same name
                        if (capabilities.get(path).get(i).getKey().equals(userName)) {
                            // if the same access do not grant
                            if (capabilities.get(path).get(i).getValue().equals(access)) {
                                System.out.println("This user have already the same access");
                                check2 = true;
                                break;
                            }
                            else{
                                capabilities.get(path).remove(i);
                                break;
                            }
                        }
                    }
                    if(!check2)
                    {
                        capabilities.get(path).add(pair);
                        System.out.println("Grant Done Successfully");
                    }

                }
                else{
                    ArrayList<Pair<String,String>> values = new ArrayList<>();
                    values.add(pair);
                    capabilities.put(path , values);
                    System.out.println("Grant Done Successfully");
                }

            }
            else
            {
                System.out.println("Directory Not Found");
            }
        }
        else
        {
            System.out.println("The user name not found");
        }
    }

    public static void saveUsersInfo() throws IOException
    {
        FileWriter myWriter = new FileWriter("user.txt");
        for (String key : users.keySet())
        {
            myWriter.write(key+","+users.get(key)+"\n");
        }
        myWriter.close();
    }
    public static void saveCapabilities()throws IOException
    {
        FileWriter myWriter = new FileWriter("capabilities.txt");
        for (String key : capabilities.keySet())
        {
            myWriter.write(key+",");
            for (int i=0; i<capabilities.get(key).size()-1; i++)
            {
                myWriter.write(capabilities.get(key).get(i).getKey()+","+capabilities.get(key).get(i).getValue()+",");
            }
            int lastIndex =capabilities.get(key).size()-1;
            myWriter.write(capabilities.get(key).get(lastIndex).getKey()+","+capabilities.get(key).get(lastIndex).getValue()+"\n");
        }
        myWriter.close();
    }

    public static void commands(Allocation obj) {

        Scanner input = new Scanner(System.in);
        String cmd = "";

        while(true) {
            System.out.print("$ ");
            cmd = input.nextLine();
            String[] cmdSections = cmd.split(" ");
            if (cmdSections[0].equalsIgnoreCase("CreateFile"))
            {
                obj.createFile(cmdSections[1], Integer.parseInt(cmdSections[2]));
            }
            else if (cmdSections[0].equalsIgnoreCase("CreateFolder"))
            {
                obj.createFolder(cmdSections[1]);
            }
            else if (cmdSections[0].equalsIgnoreCase(("DeleteFile")))
            {
                obj.deleteFile(cmdSections[1]);
            }
            else if (cmdSections[0].equalsIgnoreCase(("DeleteFolder")))
            {
                obj.deleteFolder(cmdSections[1]);
            }
            else if (cmdSections[0].equalsIgnoreCase(("DisplayDiskStatus")))
            {
                obj.displayDiskStatus();
            }
            else if (cmdSections[0].equalsIgnoreCase(("DisplayDiskStructure")))
            {
                obj.displayDiskStructure(obj.directories.get(0), 0);
            }
            else if(cmd.equalsIgnoreCase("exit"))
            {
                break;
            }
            else {
                System.out.println("Error: Invalid Command");
            }

        }
    }

    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);
        String choice;
        System.out.println("Which technique do you want to apply?");
        System.out.println("1- Contiguous allocation.");
        System.out.println("2- Linked allocation.");
        System.out.println("3- Indexed allocation.");

        choice = input.nextLine();
        Allocation obj;
       if(choice.equals("1"))
        {
            obj = new ContiguousAllocation();
            obj.loadFromFile("contiguous.vfs");
            commands(obj);
            obj.saveToFile("contiguous.vfs");

        }
        else if(choice.equals("2"))
        {
            obj= new LinkedAllocation();
            obj.loadFromFile("linked.vfs");
            commands(obj);
            obj.saveToFile("linked.vfs");
        }
        else if(choice.equals("3"))
        {
            obj = new IndexedAllocation();
            obj.loadFromFile("indexed.vfs");
            commands(obj);
            obj.saveToFile("indexed.vfs");
        }
        else
        {
            System.out.println("Error: invalid choice");
        }


    }
}