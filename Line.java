public class Line
{
    private int row, col;
    private boolean picked;
    public Line(int r,int c,boolean b)
    {
        row=r;col=c;picked=b;
    }
    public boolean getPicked()
    {
        return picked;
    }
    public int getRow()
    {
        return row;
    }
    public int getCol()
    {
        return col;
    }
    public String toString()
    {
        return "("+row+","+col+")";
    }
    public boolean equals(Line t)
    {
        return(row==t.getRow() && col==t.getCol());
    }
}