import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReceive implements Runnable {
    private Socket s;
    
    public ClientReceive(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            Thread.sleep(500);
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split("\\.");
                String info = strs[0];     
                String name = "", line = "";
                if (strs.length == 2)
                    line = strs[1];
                else if (strs.length == 3) {
                    line = strs[1];
                    name = strs[2];
                }
                if (info.equals("1")) {  // 1 para msg
                    ClientHandler.textMessage.append(line + "\r\n");
                    ClientHandler.textMessage.setCaretPosition(ClientHandler.textMessage.getText().length());
                } else if (info.equals("2") || info.equals("3")) {
                    if (info.equals("2")) {
                        ClientHandler.textMessage.append("(Thông báo) " + name + " đã vào!" + "\r\n");
                        ClientHandler.textMessage.setCaretPosition(ClientHandler.textMessage.getText().length());
                    } else {
                        ClientHandler.textMessage.append("(Thông báo) " + name + " đã thoát!" + "\r\n");
                        ClientHandler.textMessage.setCaretPosition(ClientHandler.textMessage.getText().length());
                    }
                    String list = line.substring(1, line.length() - 1);
                    String[] data = list.split(",");
                    ClientHandler.user.clearSelection();
                    ClientHandler.user.setListData(data);
                } else if (info.equals("4")) {  // 4 para alertas
                    ClientHandler.connect.setText("đăng nhập");
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    JOptionPane.showMessageDialog(ClientHandler.window, "Ai đó đã sử dụng tên người dùng này");
                    break;
                } else if (info.equals("5")) {   // 5 para fechar o servidor
                    ClientHandler.connect.setText("đã vào");
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    break;
                } else if (info.equals("6")) {  // 6 para msg privada
                    ClientHandler.textMessage.append("(Tin nhắn riêng) " + line + "\r\n");
                    ClientHandler.textMessage.setCaretPosition(ClientHandler.textMessage.getText().length());
                } else if (info.equals("7")) {
                    JOptionPane.showMessageDialog(ClientHandler.window, "Người dùng này không trực tuyến");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ClientHandler.window, "Người dùng này đã rời đi");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
