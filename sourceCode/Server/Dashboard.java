package Server;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Dashboard {
    public static JFrame window;
    public static JTextArea console;
    public static JList<String> user;

    public JButton disconec;
    public JTextField message;
    public JLabel jip;
    public JLabel jport;

    public Dashboard() {

    }

    public Dashboard(int port) {

        if (Serverhandler.ss != null && !Serverhandler.ss.isClosed()) {
            JOptionPane.showMessageDialog(window, "Server is running!");
        } else {
            if (port != 0) {
                try {
                    Serverhandler.flag = true;
                    new Thread(new Serverhandler(port)).start();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(window, "Cannot start server");
                }
            }
        }

        init(port);
    }

    public void init(int port) { // layout do servidor
        window = new JFrame("Monitoring system");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setLayout(null);
        window.setBounds(200, 200, 500, 350);
        window.setResizable(false);

        JLabel labelnomeServidor = new JLabel("IP:");
        labelnomeServidor.setBounds(10, 8, 80, 30);
        window.add(labelnomeServidor);

        jport = new JLabel(String.valueOf(port));
        jport.setBounds(80, 8, 60, 30);
        window.add(jport);

        JLabel label_porta = new JLabel("Port:");
        label_porta.setBounds(150, 8, 60, 30);
        window.add(label_porta);

        jport = new JLabel(String.valueOf(port));
        jport.setBounds(200, 8, 70, 30);
        window.add(jport);

        console = new JTextArea();
        console.setBounds(10, 70, 340, 320);
        console.setEditable(false); // não pode ser editado

        console.setLineWrap(true); // automatic content line feed
        console.setWrapStyleWord(true);

        JLabel label_text = new JLabel("Logs");
        label_text.setBounds(100, 47, 190, 30);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(console);
        paneText.setBounds(10, 70, 340, 220);
        window.add(paneText);

        JLabel label_listaUsuario = new JLabel("List client");
        label_listaUsuario.setBounds(357, 47, 180, 30);
        window.add(label_listaUsuario);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(355, 70, 130, 220);
        window.add(paneUser);

        message = new JTextField();
        message.setBounds(0, 0, 0, 0);
        window.add(message);

        disconec = new JButton("Enviar");
        disconec.setBounds(0, 0, 0, 0);
        window.add(disconec);

        myEvent(); // add listeners
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (Serverhandler.listaClient != null && Serverhandler.listaClient.size() != 0) {
                    try {
                        new ServerSend(Serverhandler.listaClient, "", "5", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0); // sair da janela
            }
        });

        disconec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Serverhandler.ss == null || Serverhandler.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "May chu da dong!");
                } else {
                    if (Serverhandler.listaClient != null && Serverhandler.listaClient.size() != 0) {
                        try {
                            new ServerSend(Serverhandler.listaClient, "", "5", "");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        disconec.setText("Dong");
                        Serverhandler.ss.close();
                        Serverhandler.ss = null;
                        Serverhandler.listaClient = null;
                        Serverhandler.flag = false; // importante
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        // inicia.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // if (Servidor.ss != null && !Servidor.ss.isClosed()) {
        // JOptionPane.showMessageDialog(window, "O servidor foi iniciado!");
        // } else {
        // ports = getPorta();
        // if (ports != 0) {
        // try {
        // Servidor.flag = true;
        // new Thread(new Servidor(ports)).start(); // inicia servidor thread
        // inicia.setText("Iniciado");
        // sair.setText("Fechar");
        // } catch (IOException e1) {
        // JOptionPane.showMessageDialog(window, "Falha ao rodar");
        // }
        // }
        // }
        // }
        // });

        message.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMsg();
                }
            }
        });

        // enviar.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // enviarMsg();
        // }
        // });
    }

    public void enviarMsg() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "Não há nada para enviar!");
        } else if (Serverhandler.listaClient == null || Serverhandler.listaClient.size() == 0) {
            JOptionPane.showMessageDialog(window, "Não há conexão!");
        } else {
            try {
                new ServerSend(Serverhandler.listaClient, "Server: " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "Falha ao enviar!");
            }
        }
    }

    // public String getnomeServidor() {
    // return nomeServidor.getText();
    // }
}
