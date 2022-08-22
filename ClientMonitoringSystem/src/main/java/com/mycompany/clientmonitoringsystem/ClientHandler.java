/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clientmonitoringsystem;

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author namtd
 */
public class ClientHandler {
    public static JFrame window;
    public static JButton connect;
    public static JTable jtable;
    public static Socket socket = null;
    public static String nameClient = "Client";
    public static DefaultTableModel jobsModel;
    public static String pathDirectory = "D:\\HOCTAP\\HOCLAI\\";

    String globalId;
    int globalPort;
    public static JLabel labelPath;
    JTextField textIp, textPort;
    JTextField jtf;
    JButton msgPrivada, enviar, btnbrowse, btnLoadLogs, btnSearch;

    public ClientHandler(String ip, int port, String name) {

        if (socket != null && socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "Connected!");
        } else {
            try {
                socket = new Socket(ip, port);
                nameClient = name;
                globalId = ip;
                globalPort = port;
                new ClientSend(socket, name, "2", "Connected", pathDirectory);
                new Thread(new ClientReceive(socket)).start();

            } catch (Exception e2) {
                JOptionPane.showMessageDialog(window, "Can't connect check ip and port");
            }
        }
        init(ip, port, name);
        new Thread(new WatchFolder(socket)).start();
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

        btnSearch = new JButton("Search");
        btnSearch.setBounds(400, 80, 150, 30);
        window.add(btnSearch);

        jtf = new JTextField("");
        jtf.setBounds(10, 80, 380, 30);
        window.add(jtf);

        btnLoadLogs = new JButton("Load Logs");
        btnLoadLogs.setBounds(1050, 80, 100, 30);
        window.add(btnLoadLogs);

        jobsModel = new DefaultTableModel(
                new String[] { "STT", "Monitoring directory", "Time", "Action", "Name Client", "Description" }, 0) {
            public Class getColumnClass(int column) {
                Class returnValue;
                if ((column >= 0) && (column < getColumnCount())) {
                    returnValue = getValueAt(0, column).getClass();
                } else {
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };

        jtable = new JTable();
        jtable.setModel(jobsModel);
        jtable.setAutoCreateRowSorter(true);
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jobsModel);
        jtable.setRowSorter(sorter);
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

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = jtf.getText();
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(text));
                }
            }
        });

        myEvent();
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
                        new ClientSend(socket, nameClient, "3", "Disconnected", pathDirectory);
                        WatchFolder.watchService.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
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
                        new Thread(new WatchFolder(socket)).start();
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
                        connect.setText("Disconnec");

                        new ClientSend(socket, getnomeUsuario(), "2", "Connected", pathDirectory);
                        new Thread(new ClientReceive(socket)).start();
                        new Thread(new WatchFolder(socket)).start();

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { jobsModel.getRowCount() + 1, pathDirectory,
                                dateFormat.format(date), "Connected",
                                nameClient,
                                "(Notification) " + nameClient + " connected to server!" };

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
                        new ClientSend(socket, getnomeUsuario(), "3", "Disconnected", pathDirectory);
                        connect.setText("Connect");
                        WatchFolder.watchService.close();
                        socket.close();
                        socket = null;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { jobsModel.getRowCount() + 1, pathDirectory,
                                dateFormat.format(date), "Disconnected",
                                nameClient,
                                "(Notification) " + nameClient + " disconnected to server!" };

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
