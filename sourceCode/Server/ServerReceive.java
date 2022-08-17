package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class ServerReceive implements Runnable {
    private Socket socket;
    private ArrayList<Socket> listClient;
    private Vector<String> nameClient;
    private Map<String, Socket> map;

    public ServerReceive(Socket s, ArrayList<Socket> listClient, Vector<String> nameClient,
            Map<String, Socket> map) {
        this.socket = s;
        this.listClient = listClient;
        this.nameClient = nameClient;
        this.map = map;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split(",,");
                String info = strs[0]; // judge the kind of info
                String line = strs[1];
                String name = "";
                if (strs.length == 3)
                    name = strs[2];

                if (info.equals("1")) { // 1 para Nova requisição de mensagem
                    Dashboard.console.append("Tin nhan moi ---->> " + line + "\r\n");
                    Dashboard.console.setCaretPosition(Dashboard.console.getText().length());
                    new ServerSend(listClient, line, "1", "");
                } else if (info.equals("2")) { // 2 para login
                    if (!nameClient.contains(line)) {
                        Dashboard.console.append("Client moi da ket noi ---->> " + line + "\r\n");
                        Dashboard.console.setCaretPosition(Dashboard.console.getText().length());
                        nameClient.add(line);
                        map.put(line, socket);
                        Dashboard.user.setListData(nameClient);
                        new ServerSend(listClient, name, "2", line);
                    } else {
                        Dashboard.console.append("Client dang nhap trung ten ---->> " + line + "\r\n");
                        Dashboard.console.setCaretPosition(Dashboard.console.getText().length());
                        listClient.remove(socket);
                        new ServerSend(socket, "", "4");
                    }
                } else if (info.equals("3")) { // 3 para sair
                    Dashboard.console.append("Da thoat ---->> " + line + "\r\n");
                    Dashboard.console.setCaretPosition(Dashboard.console.getText().length());
                    nameClient.remove(line);
                    listClient.remove(socket);
                    map.remove(line);
                    Dashboard.user.setListData(nameClient);
                    new ServerSend(listClient, nameClient, "3", line);
                    socket.close();
                    break; // quebra de info
                } else if (info.equals("4")) { // 4 para msg privada
                    Dashboard.console.append("Tin nhan moi ---->> " + line + "\r\n");
                    Dashboard.console.setCaretPosition(Dashboard.console.getText().length());
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
