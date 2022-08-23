/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.servermonitoringsystem;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
/**
 *
 * @author namtd
 */
public class ServerSend {
    ServerSend(ArrayList<Socket> listClient, Object message, String info, String name) throws IOException {
        String messages = info + "." + message + "." + name;
        PrintWriter pwOut = null;
        for (Socket s : listClient) { // gửi tin nhắn cho từng client cần thiết
            pwOut = new PrintWriter(s.getOutputStream(), true);
            pwOut.println(messages);
        }
    }

    ServerSend(Socket s, Object message, String info, String name) throws IOException {
        String messages = info + "." + message + "." + name;
        PrintWriter pwOut = new PrintWriter(s.getOutputStream(), true);
        pwOut.println(messages);
    }
}
