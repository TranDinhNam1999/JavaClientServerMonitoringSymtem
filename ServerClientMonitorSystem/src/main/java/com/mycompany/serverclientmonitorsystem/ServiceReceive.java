/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.serverclientmonitorsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author namha
 */
public class ServiceReceive implements Runnable {
    
    private Socket socket;
    private ArrayList<Socket> listaUsuario;
    private Vector<String> nomeUsuario;
    private Map<String, Socket> map;
    
    public ServiceReceive(Socket s, ArrayList<Socket> listaUsuario, Vector<String> nomeUsuario,
            Map<String, Socket> map) {
        this.socket = s;
        this.listaUsuario = listaUsuario;
        this.nomeUsuario = nomeUsuario;
        this.map = map;
    }
    
    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Dashboard cts = new Dashboard();
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split(",,");
                String info = strs[0]; // judge the kind of info
                String line = strs[1];
                // System.out.println(line);
                String name = "";
                if (strs.length == 3)
                    name = strs[2];

                if (info.equals("1")) {
                    cts.appendMessagesInTextArea("Nova mensagem ---->> " + line + "\r\n");
                    new ServerSend(listaUsuario, line, "1", "");
                } else if (info.equals("2")) { // 2 para login
                    if (!nomeUsuario.contains(line)) {
                        cts.appendMessagesInTextArea("Novo login requisitado ---->> " + line + "\r\n");
                        nomeUsuario.add(line);
                        map.put(line, socket);
                        cts.setLisClient(nomeUsuario);
                        new ServerSend(listaUsuario, nomeUsuario, "2", line);
                    } else {
                        cts.appendMessagesInTextArea("Login duplicado ---->> " + line + "\r\n");
                        listaUsuario.remove(socket);
                        new ServerSend(socket, "", "4");
                    }
                } else if (info.equals("3")) { // 3 para sair
                    cts.appendMessagesInTextArea("Saiu ---->> " + line + "\r\n");
                    nomeUsuario.remove(line);
                    listaUsuario.remove(socket);
                    map.remove(line);
                    cts.setLisClient(nomeUsuario);
                    new ServerSend(listaUsuario, nomeUsuario, "3", line);
                    socket.close();
                    break; // quebra de info
                } else if (info.equals("4")) { // 4 para msg privada
                    cts.appendMessagesInTextArea("Nova mensagem privada ---->> " + line + "\r\n");
                    if (map.containsKey(name))
                        new ServerSend(map.get(name), line, "6");
                    else
                        new ServerSend(socket, "", "7");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
