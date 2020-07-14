import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Scanner;

public class GameLogic extends Thread {

    private static GameMode gameMode; //режим игры
    PlayingField playingField;
    MouseHandler mouseHandler; //слущатель мыши
    int filledField = 0; // для определения заполненности поля
    private static boolean myTurn; // определение чей ход для режима онлайн
    private boolean turnX; //какой символ отрисовывать, если true то ход крестиков
    private char xOrO; //заполнение поля "Х" или "О"
    private boolean gameEnd = false;
    InputStream inputStream;
    OutputStream outputStream;
    Scanner scanner;
    PrintWriter out;


    GameLogic(PlayingField playingField)  {
        this.playingField = playingField;
        turnX = true;
        
        startLocalGame();
    }
    GameLogic(PlayingField playingField, InputStream inputStream, OutputStream outputStream) {
        this.playingField = playingField;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        scanner = new Scanner(inputStream);
        out = new PrintWriter(new OutputStreamWriter(outputStream), true);
        turnX = true;
        playingField.addMouseListener(mouseHandler = new MouseHandler());

        startOnlineGame();
    }

    public static void setGameMode(GameMode gameMode) {
        GameLogic.gameMode = gameMode;
    }

    public void startOnlineGame() {
        while(!gameEnd){

            if(!myTurn) {
                MainFrame.whoseTurnPanel.setText("Сейчас ход: " + charWhoseStep() + " (соперника)");

                try {
                    int n = scanner.nextInt();
                    playingField.cell[n / 3][n % 3] = charWhoseStep();
                    filledField++;
                }catch (RuntimeException e) {
                    JOptionPane.showMessageDialog(playingField, "Соединение разорвано!");
                    gameEnd = true;
                    clearField();
                    break;
                }

                playingField.repaint();

                if (checkWin()) {
                    JOptionPane.showMessageDialog(playingField, "Вы проиграли!");
                    clearField();
                    MainFrame.whoseTurnPanel.setText("Игра пока не начата");
                    MainFrame.startGameButton.setEnabled(true);
                    break;
                }
                // проверка на ничью
                if (filledField > 8){
                    standoff();
                }
                turnX = !turnX;
                myTurn = !myTurn;
            }
            else if(myTurn) {
                MainFrame.whoseTurnPanel.setText("Сейчас ход: " + charWhoseStep() + " (ваш)");
                while(true){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    if(!myTurn) break;
                }
            }
        }
        out.close();
        scanner.close();
        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void startLocalGame() {
        MainFrame.whoseTurnPanel.setText("Сейчас ход: " + charWhoseStep());
        MainFrame.whoseTurnPanel.repaint();
        playingField.addMouseListener(mouseHandler = new MouseHandler());
    }

    public char charWhoseStep(){
        if(turnX) xOrO = 'X';
        else xOrO = 'O';
        return xOrO;
    }
    // проверка на победу
    public boolean checkWin(){
        char c = charWhoseStep();
        boolean result = checkLines(c) || checkDiagonal(c);
        if(result) {
            gameEnd = true;
            playingField.removeMouseListener(mouseHandler);
        }
        return result;
    }
    public boolean checkLines(char c){
        for(int i=0; i < 3; i++){
            if(playingField.cell[i][0] == c && playingField.cell[i][1] == c && playingField.cell[i][2] == c) return true;
            if(playingField.cell[0][i] == c && playingField.cell[1][i] == c && playingField.cell[2][i] == c) return true;
        }
        return false;
    }
    public boolean checkDiagonal(char c){
        boolean b = true; //совпадает ли ячейка с символом
        boolean d = true; //совпадает ли ячейка с символом
        for (int i=0; i<3; i++){
            b = (playingField.cell[i][i] == c) && b;
            d = (playingField.cell[i][playingField.cell.length-1-i] == c) && d;
        }
        return b || d;
    }
    // действия если ничья
    public void standoff(){
        JOptionPane.showMessageDialog(playingField, "Ничья!");

        playingField.removeMouseListener(mouseHandler);
        myTurn = !myTurn;
        clearField();
        MainFrame.whoseTurnPanel.setText("Игра пока не начата");
        MainFrame.startGameButton.setEnabled(true);
        gameEnd = true;
    }
    // очистка игрового поля
    public void clearField(){
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                playingField.cell[i][j] = '\u0000';
            }
        }
        playingField.repaint();
    }

    public static void setMyTurn(boolean mt) {
        myTurn = mt;
    }

    private boolean step(int x, int y){
        boolean clicked = false;

        int i = (x-40)/playingField.getCellWidth();
        int j = (y-40)/playingField.getCellHeight();
        if((x>40 && i<3) && (y>40 && j<3)) {
            if (playingField.cell[i][j] == '\u0000') {
                playingField.cell[i][j] = charWhoseStep();
                clicked = true;
                filledField++;
                playingField.repaint();

                if (gameMode != GameMode.LOCALMODE) {

                    out.println(3 * i + j);
                }

                // проверка на победу
                if (checkWin()) {
                    if (gameMode == GameMode.LOCALMODE)
                        JOptionPane.showMessageDialog(playingField, "Игрок " + charWhoseStep() + " победил!");
                    else JOptionPane.showMessageDialog(playingField, "Вы победили!!!");
                    clearField();
                    MainFrame.whoseTurnPanel.setText("Игра пока не начата");
                    MainFrame.startGameButton.setEnabled(true);
                    return true;
                }
                // проверка на ничью
                if (filledField > 8) {
                    standoff();
                    return true;
                }
                turnX = !turnX;
            }
        }

        if(gameMode == GameMode.LOCALMODE) MainFrame.whoseTurnPanel.setText("Сейчас ход: " + charWhoseStep());
        return clicked;
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent event) {
            if (myTurn) {
                if(step(event.getX(), event.getY())) {
                    if (gameMode != GameMode.LOCALMODE) myTurn = false;
                }
            }
        }
    }
}
