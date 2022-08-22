/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servermonitoringsystem;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;

import javax.swing.JOptionPane;
/**
 *
 * @author namtd
 */
public class Serverhandler implements Runnable  {
    private int porta;
    public static ArrayList<Socket> listaClient = null;
    public static Vector<String> nameClient = null; // thread security
    public static Map<String, Socket> map = null;
    public static ServerSocket ss = null;
    public static boolean flag = true;

    public Serverhandler(int porta) throws IOException {
        this.porta = porta;
    }

    public void run() {
        Socket s = null;
        listaClient = new ArrayList<Socket>();
        nameClient = new Vector<String>();
        map = new HashMap<String, Socket>(); // name to socket one on one map

        System.out.println("Máy chủ đã khởi động!");
        try {
            ss = new ServerSocket(porta);
            System.out.println(ss.getLocalSocketAddress());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (flag) {
            try {
                s = ss.accept();
                listaClient.add(s);
                new Thread(new ServerReceive(s, listaClient, nameClient, map)).start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Dashboard.window, "Đóng máy chủ！");
            }
        }
    }
}
