/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clientmonitoringsystem;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author namtd
 */
public class ClientReceive implements Runnable {
     private Socket socket;

    public ClientReceive(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread.sleep(500);
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split("\\.");
                String info = strs[0];
                String name = strs[2], line = strs[1];
                if (info.equals("1")) {
                } else if (info.equals("2") || info.equals("3")) {
                    if (info.equals("2")) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(date), "Connected",
                                ClientHandler.nameClient,
                                "(Notification) " + ClientHandler.nameClient + " connected to server!" };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Connected" + "," +
                                ClientHandler.nameClient + "," +
                                "(Notification) " + ClientHandler.nameClient + " connected to server!" + "}";

                        ClientHandler.jobsModel.addRow(obj);
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
                                "(Notification)" + ClientHandler.nameClient + " disconnected to server!" };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Disconnected" + "," +
                                ClientHandler.nameClient + "," +
                                "(Notification) " + ClientHandler.nameClient + " disconnected to server!" + "}";

                        ClientHandler.jobsModel.addRow(obj);
                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                    }
                } else if (info.equals("4")) {
                    ClientHandler.connect.setText("Log-in");
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    JOptionPane.showMessageDialog(ClientHandler.window, "Someone used this username!!!");
                    break;
                } else if (info.equals("13")) {
                    ClientHandler.pathDirectory = line + "\\";
                    ClientHandler.labelPath.setText("Path: " + line);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(date), "Change path",
                            ClientHandler.nameClient,
                            "(Notification) Server send change path" };

                    String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                            + ClientHandler.pathDirectory + "," +
                            dateFormat.format(date).toString() + "," + "Change path" + "," +
                            ClientHandler.nameClient + "," +
                            "(Notification) Server send change path" + "}";

                    ClientHandler.jobsModel.addRow(obj);
                    ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                    WriteFile wr = new WriteFile();
                    wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);

                    WatchFolder.watchService.close();
                    new Thread(new WatchFolder(this.socket)).start();

                    break;
                } else if (info.equals("5")) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(date), "Server die",
                            ClientHandler.nameClient,
                            "(Notification) Server has been die" };

                    String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                            + ClientHandler.pathDirectory + "," +
                            dateFormat.format(date).toString() + "," + "Server die" + "," +
                            ClientHandler.nameClient + "," +
                            "(Notification) Server has been die" + "}";

                    ClientHandler.jobsModel.addRow(obj);
                    ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                    WriteFile wr = new WriteFile();
                    wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                    ClientHandler.connect.setText("Connect");
                    JOptionPane.showMessageDialog(ClientHandler.window, "Server disconnect, please connect againt");
                    WatchFolder.watchService.close();
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
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
