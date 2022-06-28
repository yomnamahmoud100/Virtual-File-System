
import java.util.Comparator;

public class Compare implements Comparator<Blocks>{
    // Consider the smaller object first since we sort based on best fit algorithm
    @Override
    public int compare(Blocks obj1, Blocks obj2) {
        if(obj1.size < obj2.size)
            return -1;
        else
            return 1;
    }
}
