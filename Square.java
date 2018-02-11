public class Square
{
    private int row,col,player;
    public Square(int r,int c,int p)
    {
        row=r;col=c;player=p;
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
    public int getPlayer()
    {
        return player;
    }
    public void setPlayer(int p)
    {player=p;}
    public boolean equals(Square s)
    {
        return(row==s.getRow() && col==s.getCol());
    }
    
}