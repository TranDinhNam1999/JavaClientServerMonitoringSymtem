/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clientmonitoringsystem;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
/**
 *
 * @author namtd
 */
public class ClientSend {
        ClientSend(Socket s, Object message, String info, String name, String path) throws IOException {
        String messages = info + ",," + message + ",," + name + ",," + path;
        PrintWriter pwOut = new PrintWriter(s.getOutputStream(), true);
        pwOut.println(messages);
    }
}
