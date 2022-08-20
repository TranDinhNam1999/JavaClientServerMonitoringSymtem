import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

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
    JLabel labelPath;
    JTextField textIp, textPort;
    JTextField jtf;
    JButton msgPrivada, enviar, btnbrowse, btnLoadLogs;
    TableRowSorter sorter;

    public ClientHandler(String ip, int port, String name) {

        if (socket != null && socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "Connected!");
        } else {
            try {
                socket = new Socket(ip, port);
                nameClient = name;
                globalId = ip;
                globalPort = port;
                new ClientSend(socket, name, "2", "");
                new Thread(new ClientReceive(socket)).start();

            } catch (Exception e2) {
                JOptionPane.showMessageDialog(window, "Can't connect check ip and port");
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

        textIp = new JTextField(ip);
        textIp.setBounds(80, 28, 70, 30);
        window.add(textIp);

        JLabel label_porta = new JLabel("port:");
        label_porta.setBounds(180, 28, 50, 30);
        window.add(label_porta);

        textPort = new JTextField(String.valueOf(port));
        textPort.setBounds(230, 28, 50, 30);
        window.add(textPort);

        JLabel labelname = new JLabel("name: " + name);
        labelname.setBounds(300, 28, 100, 30);
        window.add(labelname);

        connect = new JButton("Disconnect");
        connect.setBounds(400, 28, 150, 30);
        window.add(connect);

        labelPath = new JLabel("Path: " + pathDirectory);
        labelPath.setBounds(600, 28, 600, 30);
        window.add(labelPath);

        btnbrowse = new JButton("Browse");
        btnbrowse.setBounds(1050, 28, 100, 30);
        window.add(btnbrowse);

        JLabel label_text = new JLabel("Search");
        label_text.setBounds(10, 80, 50, 30);
        window.add(label_text);

        jtf = new JTextField("");
        jtf.setBounds(80, 80, 300, 30);
        window.add(jtf);

        btnLoadLogs = new JButton("Load Logs");
        btnLoadLogs.setBounds(1050, 80, 100, 30);
        window.add(btnLoadLogs);

        jobsModel = new DefaultTableModel(
                new String[] { "STT", "Monitoring directory", "Time", "Action", "Name Client", "Description" }, 0);

        jtable = new JTable();
        // Initializing the JTable
        jtable.setModel(jobsModel);
        jtable.setAutoCreateRowSorter(true);
        jtable.setBounds(10, 120, 1160, 300);

        TableColumnModel columnModel = jtable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(400);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(jtable);
        sp.setBounds(10, 120, 1160, 300);
        window.add(sp);

        myEvent(); // add conectados/ouvindo a porta
        window.setVisible(true);
    }

    public void writeFile(String value) {
        String PATH = pathDirectory + "\\" + nameClient + "\\";
        String directoryName = PATH;
        String fileName = "logs.txt";

        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try {
            FileOutputStream fos = new FileOutputStream(directoryName + "\\" + fileName + "\\", true);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            bufferedWriter.append(value + "\r\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(String path) {
        try {
            Scanner scan = new Scanner(new File(path), "UTF-8");
            while (scan.hasNext()) {
                String data1 = scan.nextLine();
                String data2 = data1.replaceAll("\\{", "");
                String data3 = data2.replaceAll("\\}", "");
                String[] arrOfStr = data3.split(",", -2);
                Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                        arrOfStr[1],
                        arrOfStr[2], arrOfStr[3],
                        arrOfStr[4],
                        arrOfStr[5] };

                jobsModel.addRow(obj);
            }
            jtable.setModel(jobsModel);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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

        jtf.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String value = jtf.getText();

                for (int row = 0; row <= jtable.getRowCount() - 1; row++) {

                    for (int col = 0; col <= jtable.getColumnCount() - 1; col++) {

                        if (value.equals(jtable.getValueAt(row, col))) {

                            // this will automatically set the view of the scroll in the location of the
                            // value
                            jtable.scrollRectToVisible(jtable.getCellRect(row, 0, true));

                            // this will automatically set the focus of the searched/selected row/value
                            jtable.setRowSelectionInterval(row, row);

                            for (int i = 0; i <= jtable.getColumnCount() - 1; i++) {

                                jtable.getColumnModel().getColumn(i);
                            }
                        }
                    }
                }
            }
        });

        btnbrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser myfileChooser = new JFileChooser();
                myfileChooser.setDialogTitle("select folder");
                if (Files.isDirectory(Paths.get(pathDirectory))) {
                    myfileChooser.setCurrentDirectory(new File(pathDirectory));
                }
                int findresult = myfileChooser.showOpenDialog(window);
                if (findresult == myfileChooser.APPROVE_OPTION) {
                    pathDirectory = myfileChooser.getCurrentDirectory().getAbsolutePath();
                    labelPath.setText("Path: " + pathDirectory);
                    try {
                        WatchFolder.watchService.close();
                        new Thread(new WatchFolder()).start();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

        btnLoadLogs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser myfileChooser = new JFileChooser();
                myfileChooser.setDialogTitle("select file");
                if (Files.isDirectory(Paths.get(pathDirectory))) {
                    myfileChooser.setCurrentDirectory(new File(pathDirectory));
                }
                int findresult = myfileChooser.showOpenDialog(window);
                if (findresult == myfileChooser.APPROVE_OPTION) {
                    String path = myfileChooser.getCurrentDirectory().getPath() + "\\logs.txt";
                    readFile(path);
                }
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null) {
                    try {
                        globalId = textIp.getText();
                        globalPort = Integer.parseInt(textPort.getText());
                        socket = new Socket(globalId, globalPort);
                        connect.setText("Connected");

                        new ClientSend(socket, getnomeUsuario(), "2", "");
                        new Thread(new ClientReceive(socket)).start();
                        new Thread(new WatchFolder()).start();

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { jobsModel.getRowCount() + 1, pathDirectory,
                                dateFormat.format(date), "Disconnected",
                                nameClient,
                                "(Thông báo) " + nameClient + " connected to server!" };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Connected" + "," +
                                ClientHandler.nameClient + "," +
                                "(Notification) " + ClientHandler.nameClient + " connected to the server!" + "}";

                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                        jobsModel.addRow(obj);
                        jtable.setModel(jobsModel);
                    } catch (Exception e2) {
                        JOptionPane.showMessageDialog(window, "Can't connect check ip and port");
                    }
                } else if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getnomeUsuario(), "3", "");
                        connect.setText("Disconnect");
                        WatchFolder.watchService.close();
                        socket.close();
                        socket = null;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { jobsModel.getRowCount() + 1, pathDirectory,
                                dateFormat.format(date), "Disconnected",
                                nameClient,
                                "(Thông báo) " + nameClient + " disconnected to server!" };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Disconnected" + "," +
                                ClientHandler.nameClient + "," +
                                "(Notification) " + ClientHandler.nameClient + " disconnected to the server!" + "}";

                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                        jobsModel.addRow(obj);
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