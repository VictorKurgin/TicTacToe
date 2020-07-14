import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class StartButtonLogic implements ActionListener {
    JFrame frame;

    StartButtonLogic(JFrame frame){
        this.frame = frame;
    }
    public void actionPerformed(ActionEvent e) {
        UIManager.put("OptionPane.yesButtonText", "Создать игру");
        UIManager.put("OptionPane.noButtonText", "Присоединиться к игре");
        UIManager.put("OptionPane.cancelButtonText", "Локально(человек-человек)");
        int choice = JOptionPane.showConfirmDialog(frame, "Выберите действие: ", "Client or server?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(choice == 0) {
            new Thread(() -> {String ip = null;
                try (final DatagramSocket socket = new DatagramSocket()) {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    ip = socket.getLocalAddress().getHostAddress();
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(frame, "Возникли проблемы. Проверьте соединение с интернетом, возможно проблема в нем");
                }
                try (ServerSocket serverSocket = new ServerSocket(8198)) {
                    JOptionPane.showMessageDialog(frame, "Внимание!! К сожалению, для установления соединения ваш противник должен быть в той же подсети что и вы." +
                            "\nВашему противнику надо ввести в поле адресной строки этот адрес: \n" + ip);
                    serverSocket.setSoTimeout(10000);

                    try (Socket socket = serverSocket.accept();
                         InputStream inputStream = socket.getInputStream();
                         OutputStream outputStream = socket.getOutputStream()) {
                        JOptionPane.showMessageDialog(frame, "Соединение установлено. Соперник начинает.");

                        GameLogic.setGameMode(GameMode.SERVERMODE);
                        GameLogic.setMyTurn(false);
                        new GameLogic(MainFrame.playingField, inputStream, outputStream);
                    }
                } catch (SocketTimeoutException exc) {
                    JOptionPane.showMessageDialog(frame, "Timeout. Соединение не было установлено");
                    MainFrame.startGameButton.setEnabled(true);
                }catch (BindException exc){
                    JOptionPane.showMessageDialog(frame, "Порт занят! Освободите порт 8198 для возможности сыграть");
                    MainFrame.startGameButton.setEnabled(true);
                }catch (IOException exc){
                    //System.out.println(exc);
                }}).start();
            MainFrame.startGameButton.setEnabled(false);
        }
        if(choice == 1) {
            new Thread(() -> {
                UIManager.put("OptionPane.cancelButtonText", "Cancel");
                String ip = JOptionPane.showInputDialog(frame, "Введите ip сервера");
                if (ip == null || ip.length() < 1) return;
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, 8198), 10000);
                    JOptionPane.showMessageDialog(frame, "Соединение установлено. Вы начинаете.");
                    try (InputStream inputStream = socket.getInputStream();
                         OutputStream outputStream = socket.getOutputStream()) {
                        GameLogic.setGameMode(GameMode.CLIENTMODE);
                        GameLogic.setMyTurn(true);
                        new GameLogic(MainFrame.playingField, inputStream, outputStream);
                    }
                } catch (SocketTimeoutException exc) {
                    JOptionPane.showMessageDialog(frame, "Timeout. Соединение не было установлено");
                    MainFrame.startGameButton.setEnabled(true);
                } catch (UnknownHostException exc) {
                    JOptionPane.showMessageDialog(frame, "Неверно введен ip");
                    MainFrame.startGameButton.setEnabled(true);
                } catch (IOException exception) {
                    //exception.printStackTrace();
                }
            }).start();
            MainFrame.startGameButton.setEnabled(false);
        }
        if(choice == 2){
            GameLogic.setGameMode(GameMode.LOCALMODE);
            GameLogic.setMyTurn(true);
            new GameLogic(MainFrame.playingField);
            MainFrame.startGameButton.setEnabled(false);
        }
    }
}
