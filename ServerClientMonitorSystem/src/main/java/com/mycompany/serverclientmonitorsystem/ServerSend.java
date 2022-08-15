/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.serverclientmonitorsystem;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author namha
 */
public class ServerSend {
     ServerSend(ArrayList<Socket> listaUsuario, Object message, String info, String name) throws IOException {
        String messages = info + "." + message + "." + name;
        PrintWriter pwOut = null;
        for (Socket s : listaUsuario) {   // enviar mensagem para cada cliente necessário
            pwOut = new PrintWriter(s.getOutputStream(), true);
            pwOut.println(messages);
        }
    }

    ServerSend(Socket s, Object message, String info) throws IOException {
        String messages = info + "." + message;
        PrintWriter pwOut = new PrintWriter(s.getOutputStream(), true);
        pwOut.println(messages);
    }
}
