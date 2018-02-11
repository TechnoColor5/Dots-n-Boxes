import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
/*Dots'n'Boxes
 * Made By: Daniel Mailloux Last updated: 5/25/16
 * Runs the game of Dots and Squares(info on the game can be found here: https://en.wikipedia.org/wiki/Dots_and_Boxes
 */
public class DotsAndBoxes extends Applet implements MouseMotionListener,MouseListener,Runnable,KeyListener
{
    int dots[][]=new int[11][13];
    Square squares[][]=new Square[10][12];
    ArrayList<Line> horiz=new ArrayList<Line>();
    ArrayList<Line> vert=new ArrayList<Line>();
    int player=1;
    Font scoreF =new Font("Courier",1,25);
    Font titleF =new Font("Courier",1,45);
    Font instructF =new Font("Courier",1,20);
    int mouseX=-200,mouseY=-200;
    Graphics bufferG;
    Image buffer;
    String chosen="";
    String sqString="";
    ArrayList<Square> notFilled=new ArrayList<Square>();
    boolean scored=false;
    int score1=0,score2=0;
    String winner="";
    boolean gameOver=false;
    int win=0;
    boolean instruct=true;
    Thread main=new Thread (this);
    public void init()
    {
        this.setLayout(null);
        resize(1280,700);
        buffer=createImage(this.getWidth(),this.getHeight());
        bufferG=buffer.getGraphics();
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        this.addKeyListener(this);
        for(int r=0;r<squares.length;r++)
            for(int c=0;c<squares[0].length;c++)
                squares[r][c]=new Square(r,c,0);

    }

    public void mouseMoved(MouseEvent e) {}

    public void paint (Graphics g)
    {   if(instruct)
        {
            g.setFont(instructF);
            g.drawString("Goal: Score the most points",100,100);
            g.drawString("Each player will take turns placing a vertical or horizontal line",100,140);
            g.drawString("between two unjoined dots while trying to form a 1x1 box. The player",100,160);
            g.drawString("that finishes a box scores a point and gets to go again. The game will",100,180);
            g.drawString("end when there are no more moves left.",100,200);
            g.drawString("Press space to begin",100,500);
            g.setFont(titleF);
            g.drawString("Dots'n'Boxes",200,50);
        }
        else
        {
            bufferG.setColor(Color.white);
            bufferG.fillRect(0,0,1400,800);
            bufferG.setColor(Color.black);
            outputBoard(g);
            bufferG.setFont(scoreF);
            if(player==1)bufferG.setColor(Color.red);else bufferG.setColor(Color.blue);
            bufferG.drawString("It is player "+player+"'s turn",75,200);
            bufferG.setColor(Color.red);bufferG.drawString("Player 1: "+score1,75,250);
            bufferG.setColor(Color.blue);bufferG.drawString("Player 2: "+score2,75,275);
            bufferG.drawString(winner,150,350);
            if(gameOver)
            {
                bufferG.fillRect(75,300,300,150);
                bufferG.setColor(Color.white);
                bufferG.fillRect(85,310,280,130);
                bufferG.setColor(Color.black);
                bufferG.drawString(winner,125,375);
            }

            g.drawImage(buffer,0,0,this);
        }
    }

    public void outputBoard(Graphics g)
    {
        for(int r=0;r<squares.length;r++)
            for(int c=0;c<squares[0].length;c++)
            {
                bufferG.setColor(Color.white);
                if(squares[r][c].getPlayer()==1){bufferG.setColor(Color.red);}
                else
                if(squares[r][c].getPlayer()==2){bufferG.setColor(Color.blue);}
                bufferG.fillRect(505+55*c,60+55*r,55,55);
        }
        bufferG.setColor(Color.black);
        for(int r=0;r<dots.length;r++)
            for(int c=0;c<dots[0].length;c++)
                bufferG.fillOval(500+55*c,55+55*r,10,10);
        for(Line p:horiz)
            if(p.getPicked())
                bufferG.fillRect(505+55*p.getCol(),50+55*p.getRow()+5,55,9);
        for(Line p:vert)
            if(p.getPicked())
                bufferG.fillRect(500+55*p.getCol(),55+55*p.getRow()+5,9,55);
        scored=false;
    }

    public void mouseClicked (MouseEvent e)
    {
        mouseX=e.getX();
        mouseY=e.getY();
        int col=(mouseX-500)/55;
        int row=(mouseY-55)/55;
        Rectangle horizRect[]=new Rectangle[11];
        Rectangle vertRect[]=new Rectangle[13];
        Rectangle mouseRect=new Rectangle(mouseX,mouseY,1,1);
        Rectangle dotRect[][]=new Rectangle[11][13];
        boolean goodMove=false,onLine=false;
        for(int r=0;r<dotRect.length;r++)
            for(int c=0;c<dotRect[0].length;c++)
                dotRect[r][c]=new Rectangle(500+55*c,55+55*r,10,10);
        for(int r=0;r<11;r++)
            horizRect[r]=new Rectangle(505,55+55*r,660,9);
        for(int v=0;v<13;v++)
            vertRect[v]=new Rectangle(500+55*v,58,9,555);
        goodMove=onDot(dotRect,mouseRect);
        if(goodMove==true)
        {
            for(int p=0;p<11;p++)
                if(mouseRect.intersects(horizRect[p]))
                {
                    Line temp=new Line(row,col,true);
                    boolean alreadyPicked=false;
                    for(Line t:horiz)
                        if(temp.equals(t))
                            alreadyPicked=true;
                    if(!alreadyPicked)
                    {
                        horiz.add(temp);
                        chosen="row "+row+" col "+col;
                        onLine=true;
                    }
            }
            for(int v=0;v<13;v++)
                if(mouseRect.intersects(vertRect[v]))
                {
                    Line temp=new Line(row,col,true);
                    boolean alreadyPicked=false;
                    for(Line t:vert)
                        if(temp.equals(t))
                            alreadyPicked=true;
                    if(!alreadyPicked)
                    {
                        vert.add(temp);
                        chosen="row "+row+" col "+col;
                        onLine=true;
                    }
            }
        }
        for(int r=0;r<squares.length;r++)
            for(int c=0;c<squares[0].length;c++)
            {
                if(squares[r][c].getPlayer()==0)
                    squares[r][c]=surrounded(squares[r][c]);
        }
        if(!scored && onLine)
        {
            if(player==1)
                player=2;
            else player=1;
        }
        onLine=false;
        if(score1+score2==120)
        {
            if(score1>score2)
            {
                winner="Player 1 wins!";
                win=1;
            }else
            if(score2>score1)
            {
                winner="Player 2 wins!";
                win=2;
            }
            else {winner="It's a draw!";win=3;}
            gameOver=true;
            main.start();
        }
        repaint();
    }

    public boolean onDot(Rectangle dotRect[][],Rectangle mouseRect)
    {
        for(int r=0;r<dotRect.length;r++)
            for(int c=0;c<dotRect[0].length;c++)
                if(mouseRect.intersects(dotRect[r][c]))
                    return false;
        return true;
    }

    public Square surrounded(Square s)
    {
        int row=s.getRow(),col=s.getCol();
        sqString=row+" "+col;
        ArrayList<Line> needH=new ArrayList<Line>();
        ArrayList<Line> needV=new ArrayList<Line>();
        needH.add(new Line(row,col,true));
        needH.add(new Line(row+1,col,true));
        needV.add(new Line(row,col,true));
        needV.add(new Line(row,col+1,true));
        int pickedCt=0;
        for(Line t:horiz)
        {
            for(int p=0;p<needH.size();p++)
                if(t.equals(needH.get(p)))
                {
                    needH.remove(p);
                    p--;
                    pickedCt++;
            }
        }
        for(Line t:vert)
        {
            for(int p=0;p<needV.size();p++)
                if(t.equals(needV.get(p)))
                {
                    needV.remove(p);
                    p--;
                    pickedCt++;
            }
        }
        if(pickedCt==4)
        {
            s.setPlayer(player);
            scored=true;
            if(player==1)
                score1++;
            else score2++;
        }
        return s;
    }

    public void run()
    {
        //MAKES AWESOME REALLY COOL ENDING ANIMATION
        //MUCH ANIMATION, SUCH PRETTY, MUCH WOW
        while(gameOver)
        {
            for(int r=0;r<squares.length;r++)
                for(int c=0;c<squares[0].length;c++)
                {
                    squares[r][c].setPlayer(win);
                    repaint();
                    try{ main.sleep(50);}
                    catch (Exception e){}
            }
            for(int p=0;p<5;p++)
            {
                for(int r=0;r<squares.length;r++)
                    for(int c=0;c<squares[0].length;c++)
                    {
                        if(win==1)
                            squares[r][c].setPlayer(2);
                        if(win==2)
                            squares[r][c].setPlayer(1);

                        repaint();
                }
                try{ main.sleep(500);}
                catch (Exception e){}
                for(int r=0;r<squares.length;r++)
                    for(int c=0;c<squares[0].length;c++)
                    {
                        squares[r][c].setPlayer(win);
                        repaint();
                }
                try{ main.sleep(500);}
                catch (Exception e){}
            }
            for(int r=0;r<squares.length;r++)
                for(int c=0;c<squares[0].length;c++)
                {
                    squares[r][c].setPlayer(0);
                    repaint();
                    try{ main.sleep(100);}
                    catch (Exception e){}
            }
            gameOver=false;
        }
    }

    public void keyPressed(KeyEvent e)
    {    
        int code=e.getKeyCode(); 
        if(code==e.VK_SPACE)
            if(instruct)
            {
                instruct=false;
                repaint();
            }
    }   

    public void keyReleased(KeyEvent e) { }

    public void keyTyped (KeyEvent e){}

    public void mouseDragged(MouseEvent e){}

    public void mousePressed (MouseEvent e) {}

    public void mouseReleased (MouseEvent e) {}

    public void mouseEntered (MouseEvent e) {}

    public void mouseExited (MouseEvent e) {}
}