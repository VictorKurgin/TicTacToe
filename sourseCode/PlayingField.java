import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

class PlayingField extends JPanel {
    private int WIDTH = MainFrame.getWIDTH();
    private int HEIGHT = MainFrame.getHEIGHT();
    Rectangle2D[] squares; //ячейка содержащий курсор мыши
    int indent = 40; //отступ от краев фрейма для заполнения игрового поля
    private int cellWidth; //ширина ячейки
    private int cellHeight; //высота ячейки
    public char[][] cell = new char[3][3];

    PlayingField(){
        squares = new Rectangle2D[9];
        setSize(WIDTH,HEIGHT);

    }

    public int getCellWidth(){
        return cellWidth;
    }
    public int getCellHeight(){
        return cellHeight;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        cellWidth = (getWidth() - 2 * indent)/3;
        cellHeight = (getHeight() - 2 * indent)/3;
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                squares[3*i+j] = new Rectangle2D.Double(indent + i * cellWidth, indent + j * cellHeight, cellWidth, cellHeight);
                g2.draw(squares[3*i+j]);
                if(cell[i][j] == 'X'){
                    g2.draw(new Line2D.Double(indent + i *cellWidth + 25, indent + j*cellHeight + 15, indent + (i+1)*cellWidth - 25, indent + (j+1)*cellHeight - 15));
                    g2.draw(new Line2D.Double(indent + i *cellWidth + 25, indent + (j+1)*cellHeight - 15, indent + (i+1)*cellWidth - 25, indent + j*cellHeight + 15));
                }
                if(cell[i][j] == 'O'){
                    g2.drawOval(indent + 15 + i * cellWidth, indent + 15 + j * cellHeight, cellWidth - 30, cellHeight - 30);
                }
            }
        }
    }
}
