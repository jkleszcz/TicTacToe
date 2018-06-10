import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class DrawPanel extends JPanel implements MouseListener{
    private Rectangle[][] fields;
    private ArrayList<Point> oFields = new ArrayList<>();
    private ArrayList<Point> xFields = new ArrayList<>();
    private Image oImage;
    private Image xImage;

    private GameField gameField;

    DrawPanel(){
        addMouseListener(this);
        setBackground(new Color(250,250,30));
        this.oImage = Toolkit.getDefaultToolkit().getImage("O.png");
        this.xImage = Toolkit.getDefaultToolkit().getImage("X.png");

        this.gameField = new GameField();

    }

    @Override
    public void mouseClicked(MouseEvent e){
        int row = (e.getY()/50)-1;
        int column = (e.getX()/50)-1;
        if(!gameField.gameIsOver && this.OXinRange(row, column) && gameField.isEmpty(row,column)) {
            //Ruch Gracza
            this.oFields.add(new Point((column + 1) * 50 + 1, (row + 1) * 50 + 1));
            gameField.changeFieldStatus(row,column, Field.FieldStatus.O);
            repaint();
            if(gameField.gameOver())
                return;
            gameField.player.addFieldsToConsider(row,column,gameField.player.consideredFields);


            //Ruch Gracz CPU
            Point computerTurn = gameField.player.drawX();
            this.xFields.add(new Point((computerTurn.y + 1) * 50 + 1, (computerTurn.x + 1) * 50 + 1));
            gameField.changeFieldStatus(computerTurn.x,computerTurn.y, Field.FieldStatus.X);
            if(gameField.gameOver())
                return;
            gameField.player.addFieldsToConsider(computerTurn.x,computerTurn.y,gameField.player.consideredFields);
            repaint();
        }
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        for(int i=0 ; i<10 ; ++i){
            for(int j=0 ; j<10 ; ++j){
                g2d.setColor(new Color(255,255,255));
                g2d.fill(fields[i][j]);
                g2d.setColor(new Color(0,0,0));
                g2d.draw(fields[i][j]);
            }
        }
        for(Point p: this.oFields){
            g2d.drawImage(this.oImage,p.x,p.y,48,48,this);
        }
        for(Point p: this.xFields){
            g2d.drawImage(this.xImage,p.x,p.y,48,48,this);
        }

    }

    public void addElements(){
        this.fields = new Rectangle[10][];
        for(int i=0 ; i<10 ; ++i){
            fields[i] = new Rectangle[10];
            for(int j=0 ; j<10 ; ++j){
                fields[i][j] = new Rectangle((i*50)+50,(j*50)+50, 50, 50);
            }
        }
    }

    private boolean OXinRange(int row, int col){
        if(row >= 0 && row < 10 && col >=0 && col < 10)
            return true;
        return false;
    }


    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent e){
    }
}
