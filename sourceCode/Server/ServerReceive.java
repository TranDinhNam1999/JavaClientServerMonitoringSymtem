package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                String info = strs[0]; // numbet
                String line = strs[1]; // name client
                String name = strs[2]; // message
                String path = strs[3]; // path

                if (info.equals("1")) {
                    new ServerSend(listClient, line, "1", "");
                } else if (info.equals("2")) { // 2 para login
                    if (!nameClient.contains(line)) {
                        nameClient.add(line);
                        Dashboard.map.put(line, socket);
                        Dashboard.mapPath.put(line, path);
                        Dashboard.user.setListData(nameClient);
                        new ServerSend(listClient, name, "2", line);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { Dashboard.jobsModel.getRowCount() + 1, path,
                                dateFormat.format(date), "Connected",
                                line,
                                name };

                        String data = "{" + (Dashboard.jobsModel.getRowCount() + 1) + ","
                                + path + "," +
                                dateFormat.format(date).toString() + "," + "Connected" + "," +
                                line + "," +
                                name + "}";

                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), Dashboard.pathDirectory);
                        Dashboard.jobsModel.addRow(obj);
                        Dashboard.jtable.setModel(Dashboard.jobsModel);
                    } else {
                        listClient.remove(socket);
                        new ServerSend(socket, "", "4", "server");
                    }
                } else if (info.equals("3")) {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    Object[] obj = new Object[] { Dashboard.jobsModel.getRowCount() + 1, path,
                            dateFormat.format(date), "Disconnected",
                            line,
                            name };

                    String data = "{" + (Dashboard.jobsModel.getRowCount() + 1) + ","
                            + path + "," +
                            dateFormat.format(date).toString() + "," + "Disconnected" + "," +
                            line + "," +
                            name + "}";

                    WriteFile wr = new WriteFile();
                    wr.writeFile(String.valueOf(data), Dashboard.pathDirectory);
                    Dashboard.jobsModel.addRow(obj);
                    Dashboard.jtable.setModel(Dashboard.jobsModel);

                    nameClient.remove(line);
                    listClient.remove(socket);
                    Dashboard.map.remove(line);
                    Dashboard.mapPath.remove(line);
                    Dashboard.user.setListData(nameClient);
                    new ServerSend(listClient, nameClient, "3", line);
                    socket.close();
                    break; // quebra de info
                } else if (info.equals("10")) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    Object[] obj = new Object[] { Dashboard.jobsModel.getRowCount() + 1, path,
                            dateFormat.format(date), "Created",
                            line,
                            name };

                    String data = "{" + (Dashboard.jobsModel.getRowCount() + 1) + ","
                            + path + "," +
                            dateFormat.format(date).toString() + "," + "Created" + "," +
                            line + "," +
                            name + "}";

                    WriteFile wr = new WriteFile();
                    wr.writeFile(String.valueOf(data), Dashboard.pathDirectory);
                    Dashboard.jobsModel.addRow(obj);
                    Dashboard.jtable.setModel(Dashboard.jobsModel);

                } else if (info.equals("11")) {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    Object[] obj = new Object[] { Dashboard.jobsModel.getRowCount() + 1, path,
                            dateFormat.format(date), "Deleted",
                            line,
                            name };

                    String data = "{" + (Dashboard.jobsModel.getRowCount() + 1) + ","
                            + path + "," +
                            dateFormat.format(date).toString() + "," + "Deleted" + "," +
                            line + "," +
                            name + "}";

                    WriteFile wr = new WriteFile();
                    wr.writeFile(String.valueOf(data), Dashboard.pathDirectory);
                    Dashboard.jobsModel.addRow(obj);
                    Dashboard.jtable.setModel(Dashboard.jobsModel);

                } else if (info.equals("12")) {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    Object[] obj = new Object[] { Dashboard.jobsModel.getRowCount() + 1, path,
                            dateFormat.format(date), "Modified",
                            line,
                            name };

                    String data = "{" + (Dashboard.jobsModel.getRowCount() + 1) + ","
                            + path + "," +
                            dateFormat.format(date).toString() + "," + "Modified" + "," +
                            line + "," +
                            name + "}";

                    WriteFile wr = new WriteFile();
                    wr.writeFile(String.valueOf(data), Dashboard.pathDirectory);
                    Dashboard.jobsModel.addRow(obj);
                    Dashboard.jtable.setModel(Dashboard.jobsModel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
