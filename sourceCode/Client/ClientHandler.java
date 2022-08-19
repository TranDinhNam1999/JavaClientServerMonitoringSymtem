import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientHandler {
    public static JFrame window;
    public static JButton connect;
    public static JTextArea textMessage;
    public static Socket socket = null;
    public static JList<String> user;
    public static String nameClient = "Client";

    JLabel labelip, labelport;
    JTextField nomeUsuario, porta, message, msgPriv;
    JButton msgPrivada, enviar;

    public ClientHandler(String ip, int port, String name) {

        if (socket != null && socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "Đã kết nối!");
        } else {
            try {
                socket = new Socket(ip, port);
                nameClient = name;
                new ClientSend(socket, name, "2", "");
                new Thread(new ClientReceive(socket)).start();

            } catch (Exception e2) {
                JOptionPane.showMessageDialog(window, "Không kết nối được kiểm tra ip và cổng");
            }
        }
        init(ip, port, name);
    }

    public void init(String ip, int port, String name) {
        window = new JFrame("Client monitoring");
        window.setLayout(null);
        window.setBounds(400, 400, 550, 480);
        window.setResizable(false);

        JLabel label_ip = new JLabel("Ip:");
        label_ip.setBounds(10, 28, 70, 30);
        window.add(label_ip);

        labelip = new JLabel(ip);
        labelip.setBounds(80, 28, 70, 30);
        window.add(labelip);

        JLabel label_porta = new JLabel("port:");
        label_porta.setBounds(180, 28, 50, 30);
        window.add(label_porta);

        labelport = new JLabel(String.valueOf(port));
        labelport.setBounds(230, 28, 50, 30);
        window.add(labelport);

        JLabel labelname = new JLabel("name: " + name);
        labelname.setBounds(300, 28, 100, 30);
        window.add(labelname);

        connect = new JButton("Disconnect");
        connect.setBounds(400, 28, 100, 30);
        window.add(connect);

        textMessage = new JTextArea(" ");
        textMessage.setBounds(10, 70, 340, 220);
        textMessage.setEditable(false);

        textMessage.setLineWrap(true);
        textMessage.setWrapStyleWord(true);

        JLabel label_text = new JLabel("Thông tin logs");
        label_text.setBounds(100, 58, 200, 50);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(textMessage);
        paneText.setBounds(10, 90, 360, 240);
        window.add(paneText);

        JLabel label_listaUsuario = new JLabel("Danh sách người dùng");
        label_listaUsuario.setBounds(380, 58, 200, 50);
        window.add(label_listaUsuario);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(375, 90, 140, 240);
        window.add(paneUser);

        JLabel label_Alerta = new JLabel("Nhập messasge cho nhóm");
        label_Alerta.setBounds(10, 320, 180, 50);
        window.add(label_Alerta);

        message = new JTextField();
        message.setBounds(10, 355, 188, 30);
        message.setText(null);
        window.add(message);

        JLabel label_Aviso = new JLabel("Thêm người dùng cho tin nhắn riêng tư");
        label_Aviso.setBounds(272, 320, 250, 50);
        window.add(label_Aviso);

        msgPriv = new JTextField();
        msgPriv.setBounds(272, 355, 100, 30);
        window.add(msgPriv);

        msgPrivada = new JButton("Msg Privada");
        msgPrivada.setBounds(376, 355, 140, 30);
        window.add(msgPrivada);

        enviar = new JButton("Grupo");
        enviar.setBounds(190, 355, 77, 30);
        window.add(enviar);

        myEvent(); // add conectados/ouvindo a porta
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, nameClient, "3", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null) {
                    JOptionPane.showMessageDialog(window, "Kết nối đã đóng!");
                } else if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getnomeUsuario(), "3", "");
                        connect.setText("Entrar");
                        connect.setText("saiu!");
                        socket.close();
                        socket = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket != null && socket.isConnected()) {
                    JOptionPane.showMessageDialog(window, "Conectado!");
                } else {
                    String ipString = "127.0.0.1";
                    String portaClinet = porta.getText();
                    String name = nomeUsuario.getText();

                    if ("".equals(name) || "".equals(portaClinet)) {
                        JOptionPane.showMessageDialog(window, "O usuário ou a portaa não podem estar vazios!");
                    } else {
                        try {
                            int portas = Integer.parseInt(portaClinet);
                            socket = new Socket(ipString, portas);
                            connect.setText("Entrou");
                            connect.setText("sair");
                            new ClientSend(socket, getnomeUsuario(), "2", "");
                            new Thread(new ClientReceive(socket)).start();
                        } catch (Exception e2) {
                            JOptionPane.showMessageDialog(window, "falha em conectar-se, verifique o ip e a portaa");
                        }
                    }
                }
            }
        });

        msgPrivada.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enviarMsgmsgPriv();
            }
        });

        enviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enviarMsg();
            }
        });
    }

    public void enviarMsg() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "Não há nada para enviar");
        } else if (socket == null || !socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "Sem conexão");
        } else {
            try {
                new ClientSend(socket, getnomeUsuario() + ": " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "falha ao enviar!");
            }
        }
    }

    public void enviarMsgmsgPriv() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "Não há nada para enviar!");
        } else if (socket == null || !socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "Sem conexão");
        } else {
            try {
                new ClientSend(socket, getnomeUsuario() + ": " + messages, "4", getmsgPrivada());
                ClientHandler.textMessage.append(getnomeUsuario() + ": " + messages + "\r\n");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "Mensagem privada não enviado!");
            }
        }
    }

    String getnomeUsuario() {
        return nameClient;
    }

    public String getmsgPrivada() {
        return msgPriv.getText();
    }
}