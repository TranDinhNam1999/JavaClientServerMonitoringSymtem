import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class ClientHandler {
    public static JFrame window;
    public static JButton connect;
    public static JTable jtable;
    public static Socket socket = null;
    public static JList<String> user;
    public static String nameClient = "Client";
    public static DefaultTableModel jobsModel;
    public static String pathDirectory = "D:\\HOCTAP\\HOCLAI";

    String globalId;
    int globalPort;
    JLabel labelip, labelport;
    JTextField nomeUsuario, porta, message, msgPriv;
    JButton msgPrivada, enviar;

    public ClientHandler(String ip, int port, String name) {

        if (socket != null && socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "Đã kết nối!");
        } else {
            try {
                socket = new Socket(ip, port);
                nameClient = name;
                globalId = ip;
                globalPort = port;
                new ClientSend(socket, name, "2", "");
                new Thread(new ClientReceive(socket)).start();

            } catch (Exception e2) {
                JOptionPane.showMessageDialog(window, "Không kết nối được kiểm tra ip và cổng");
            }
        }
        init(ip, port, name);
        new Thread(new WatchFolder()).start();
    }

    public void init(String ip, int port, String name) {
        window = new JFrame("Client monitoring");
        window.setLayout(null);
        window.setBounds(400, 400, 1200, 480);
        window.setResizable(true);

        JLabel label_ip = new JLabel("Ip:");
        label_ip.setBounds(10, 28, 70, 30);
        window.add(label_ip);

        labelip = new JLabel(ip);
        labelip.setBounds(80, 28, 70, 30);
        window.add(labelip);

        JLabel label_porta = new JLabel("port:");
        label_porta.setBounds(180, 28, 50, 30);
        window.add(label_porta);

        labelport = new JLabel(String.valueOf(port));
        labelport.setBounds(230, 28, 50, 30);
        window.add(labelport);

        JLabel labelname = new JLabel("name: " + name);
        labelname.setBounds(300, 28, 100, 30);
        window.add(labelname);

        connect = new JButton("Disconnect");
        connect.setBounds(400, 28, 150, 30);
        window.add(connect);

        JLabel label_text = new JLabel("Thông tin logs");
        label_text.setBounds(10, 58, 200, 50);
        window.add(label_text);

        jobsModel = new DefaultTableModel(
                new String[] { "STT", "Monitoring directory", "Time", "Action", "Name Client", "Description" }, 0);

        jtable = new JTable();
        // Initializing the JTable
        jtable.setModel(jobsModel);
        jtable.setBounds(10, 90, 1160, 300);

        TableColumnModel columnModel = jtable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(400);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(jtable);
        sp.setBounds(10, 90, 1160, 300);
        window.add(sp);

        myEvent(); // add conectados/ouvindo a porta
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, nameClient, "3", "");
                        WatchFolder.watchService.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null) {
                    try {
                        socket = new Socket(globalId, globalPort);
                        connect.setText("Đóng kết nối");
                        new ClientSend(socket, getnomeUsuario(), "2", "");
                        new Thread(new ClientReceive(socket)).start();
                        new Thread(new WatchFolder()).start();
                    } catch (Exception e2) {
                        JOptionPane.showMessageDialog(window, "Không kết nối được, kiểm tra ip và cổng");
                    }
                } else if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getnomeUsuario(), "3", "");
                        connect.setText("Kết nối lại");
                        WatchFolder.watchService.close();
                        socket.close();
                        socket = null;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        jobsModel.addRow(
                                new Object[] { jobsModel.getRowCount() + 1, pathDirectory,
                                        dateFormat.format(date), "Disconnected",
                                        nameClient,
                                        "(Thông báo) " + nameClient + " đã ngắt kết nối với server!" });
                        jtable.setModel(jobsModel);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    String getnomeUsuario() {
        return nameClient;
    }
}