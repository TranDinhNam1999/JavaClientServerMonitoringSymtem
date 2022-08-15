/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.serverclientmonitorsystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author namha
 */
public class ServerCreate implements Runnable {
    
    private int porta;
    public static ArrayList<Socket> listaUsuario = null;
    public static Vector<String> nomeUsuario = null; // thread security
    public static Map<String, Socket> map = null;
    public static ServerSocket ss = null;
    public static boolean flag = true;
    
    public ServerCreate(int porta) throws IOException {
       this.porta = porta;
    }

    @Override
    public void run() {
        Socket s = null;
        listaUsuario = new ArrayList<Socket>(); // Contém portaas dos usuários
        nomeUsuario = new Vector<String>(); // contém usuários
        map = new HashMap<String, Socket>(); // name to socket one on one map

        System.out.println("Servidor iniciado!");

        try {
            ss = new ServerSocket(porta);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (flag) {
            try {
                s = ss.accept();
                listaUsuario.add(s);
                new Thread(new ServiceReceive(s, listaUsuario, nomeUsuario, map)).start();
            } catch (IOException e) {
                Dashboard cts = new Dashboard();
                JOptionPane.showMessageDialog(cts, "Servidor encerrado！");
            }
        }
    }
    
}
