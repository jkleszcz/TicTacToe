import javax.swing.*;

public class Main {

    public static void main(String[] argv){
        JFrame frame = new JFrame("Tic Tac Toe");

        DrawPanel gameField = new DrawPanel();
        gameField.addElements();

        frame.setContentPane(gameField);

        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
