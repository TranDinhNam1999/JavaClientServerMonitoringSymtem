import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                if (info.equals("1")) {
                } else if (info.equals("2") || info.equals("3")) {
                    if (info.equals("2")) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(date), "Connected",
                                ClientHandler.nameClient,
                                "(Thông báo) " + ClientHandler.nameClient + " connected to server!" };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Connected" + "," +
                                ClientHandler.nameClient + "," +
                                "(Notification) " + ClientHandler.nameClient + " connected to server!" + "}";

                        ClientHandler.jobsModel.addRow(
                                obj);
                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                    } else {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(date), "Disconnected",
                                ClientHandler.nameClient,
                                "(Thông báo) " + ClientHandler.nameClient + " disconnected to server!" };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Disconnected" + "," +
                                ClientHandler.nameClient + "," +
                                "(Notification) " + ClientHandler.nameClient + " disconnected to server!" + "}";

                        ClientHandler.jobsModel.addRow(
                                obj);
                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                    }
                    String list = line.substring(1, line.length() - 1);
                    String[] data = list.split(",");
                    ClientHandler.user.clearSelection();
                    ClientHandler.user.setListData(data);
                } else if (info.equals("4")) {
                    ClientHandler.connect.setText("Log-in");
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    JOptionPane.showMessageDialog(ClientHandler.window, "Someone used this username!!!");
                    break;
                } else if (info.equals("5")) {
                    break;
                } else if (info.equals("6")) {
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ClientHandler.window, "This user has left!!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
