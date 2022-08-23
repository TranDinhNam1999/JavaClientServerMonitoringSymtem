/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servermonitoringsystem;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author namtd
 */
public class Dashboard {
    public static JFrame window;
    public static JList<String> user;
    public static Map<String, String> mapPath = new HashMap<String, String>();
    public static Map<String, Socket> map = new HashMap<String, Socket>();;
    public static String address;
    public static DefaultTableModel jobsModel;
    public static JTable jtable;
    public static String pathDirectory = "D:\\HOCTAP\\HOCLAI\\LOGS\\";

    public JButton disconec, btnSearch, btnLoadLogs;
    public JTextField message, jtf;
    public JLabel jip;
    public JLabel jport;

    public Dashboard() {

    }

    public Dashboard(int port) {

        if (Serverhandler.ss != null && !Serverhandler.ss.isClosed()) {
            JOptionPane.showMessageDialog(window, "Server is running!");
        } else {
            if (port != 0) {
                try {
                    Serverhandler.flag = true;
                    address = InetAddress.getLocalHost().getHostAddress();
                    new Thread(new Serverhandler(port)).start();
                    pathDirectory = Paths.get(".").normalize().toAbsolutePath().toString();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(window, "Cannot start server");
                }
            }
        }
        init(port);
    }

    public void init(int port) { // layout do servidor
        window = new JFrame("Monitoring system");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setLayout(null);
        window.setBounds(200, 200, 1200, 480);
        window.setResizable(false);

        JLabel labelnomeServidor = new JLabel("IP:");
        labelnomeServidor.setBounds(10, 8, 80, 30);
        window.add(labelnomeServidor);

        jport = new JLabel(String.valueOf(address));
        jport.setBounds(30, 8, 100, 30);
        window.add(jport);

        JLabel label_porta = new JLabel("Port:");
        label_porta.setBounds(150, 8, 60, 30);
        window.add(label_porta);

        jport = new JLabel(String.valueOf(port));
        jport.setBounds(200, 8, 70, 30);
        window.add(jport);

        JLabel label_text = new JLabel("List client");
        label_text.setBounds(10, 80, 100, 30);
        window.add(label_text);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(530, 70, 150, 30);
        window.add(btnSearch);

        jtf = new JTextField("");
        jtf.setBounds(147, 70, 380, 30);
        window.add(jtf);

        btnLoadLogs = new JButton("Load Logs");
        btnLoadLogs.setBounds(1071, 70, 100, 30);
        window.add(btnLoadLogs);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(10, 110, 130, 320);

        user.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    JList source = (JList) event.getSource();
                    String selected = source.getSelectedValue().toString();

                    JFileChooser myfileChooser = new JFileChooser();
                    myfileChooser.setDialogTitle("select folder");
                    if (Files.isDirectory(Paths.get(mapPath.get(selected)))) {
                        myfileChooser.setCurrentDirectory(new File(mapPath.get(selected)));
                    }
                    int findresult = myfileChooser.showOpenDialog(window);
                    if (findresult == myfileChooser.APPROVE_OPTION) {
                        String pathClient = myfileChooser.getCurrentDirectory().getAbsolutePath();
                        try {
                            new ServerSend(map.get(selected), pathClient, "13", "Server");
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();

                            Object[] obj = new Object[] { jobsModel.getRowCount() + 1, pathClient,
                                    dateFormat.format(date), "Change path",
                                    selected,
                                    "Change path monitoring systtem" };

                            String data = "{" + (jobsModel.getRowCount() + 1) + ","
                                    + pathClient + "," +
                                    dateFormat.format(date).toString() + "," + "Change path" + "," +
                                    selected + "," +
                                    "Change path monitoring systtem" + "}";

                            WriteFile wr = new WriteFile();
                            wr.writeFile(String.valueOf(data), pathDirectory);
                            jobsModel.addRow(obj);
                            jtable.setModel(jobsModel);

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }
                    System.out.println(selected);
                }
            }
        });

        window.add(paneUser);

        message = new JTextField();
        message.setBounds(0, 0, 0, 0);
        window.add(message);

        disconec = new JButton("Disconnect");
        disconec.setBounds(0, 0, 0, 0);
        window.add(disconec);

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
        jtable.setBounds(145, 110, 1030, 320);

        TableColumnModel columnModel = jtable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(400);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(jtable);
        sp.setBounds(145, 110, 1030, 320);
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
        String PATH = pathDirectory;
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
                Object[] obj = new Object[] { jobsModel.getRowCount() + 1,
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
                if (Serverhandler.listaClient != null && Serverhandler.listaClient.size() != 0) {
                    try {
                        new ServerSend(Serverhandler.listaClient, "Server die", "5", "Server");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
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

        disconec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Serverhandler.ss == null || Serverhandler.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "Máy chủ đã đóng!");
                } else {
                    if (Serverhandler.listaClient != null && Serverhandler.listaClient.size() != 0) {
                        try {
                            new ServerSend(Serverhandler.listaClient, "", "5", "");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        disconec.setText("Đóng");
                        Serverhandler.ss.close();
                        Serverhandler.ss = null;
                        Serverhandler.listaClient = null;
                        Serverhandler.flag = false; // importante
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
}
