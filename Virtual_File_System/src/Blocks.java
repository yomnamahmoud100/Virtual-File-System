public class Blocks {
    public int start ;
    public int end ;
    public int size;
    public Blocks(int start , int end)  //end is contained
    {
        this.start = start;
        this.end = end;
        this.size = end - start + 1;
    }

    @Override
    public String toString() {
        return "Blocks{" +
                "start=" + start +
                ", end=" + end +
                ", size=" + size +
                '}';
    }
}
