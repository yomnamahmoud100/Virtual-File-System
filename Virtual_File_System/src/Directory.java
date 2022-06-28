import java.util.ArrayList;

public class Directory {
    private String name;
    private String result = "";
    public String directoryPath;
    public Directory parentDir ;
    public ArrayList<File2> files = new ArrayList<>();
    public ArrayList<Directory> subDir = new ArrayList<>();
    public boolean deleted = false;

    public Directory()
    {

    }

    public Directory(String name , String directoryPath, boolean deleted) {
        this.name = name;
        this.directoryPath = directoryPath;
        this.deleted = deleted;
    }

    public String printDirectoryStructure(String result, int level) {
	/*this method prints the directory name and its files
    then makes recursion to loop on the subDirectories to print their structure too.

    The level parameter can be used to print spaces before the directory name is printed to show its level in the structure */
        for(int k=0 ; k<level*3; k++) result+=" ";
        result+=this.name;
        result+='\n';
        if(this.files.size()>0){
            for(int i=0 ; i<files.size();i++){
                for(int k=0 ; k<(level+1)*3; k++)  result+=" ";
                result += files.get(i).getName();
                result+='\n';
            }
        }
        if(this.subDir.size()==0) return result;
        for(int i=0 ; i<this.subDir.size();i++){
            result+=this.subDir.get(i).printDirectoryStructure("", level+1);
        }
        return result;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public String getName() {
        return name;
    }

    public ArrayList<File2> getFiles() {
        return files;
    }

    public ArrayList<Directory> getSubDir() {
        return subDir;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        result += name + "\n";

        if(files.size() !=0  && subDir.size() == 0)
            for (int i = 0; i < files.size(); i++){
                result += "\t" + files.get(i).getName() + "\n";
            }

        if(subDir.size() != 0 && files.size() == 0) {
            for (int i = 0; i < subDir.size(); i++){
                result += "\t" +subDir.get(i).toString() ;
            }
        }

        if(subDir.size() != 0 && files.size() != 0) {
            for (int i = 0; i < files.size(); i++){
                result += "\t" + files.get(i).getName() + "\n";
            }

            for (int i = 0; i < subDir.size(); i++){
                result += "\t" + subDir.get(i).toString();
            }
        }

        return result;

    }

}