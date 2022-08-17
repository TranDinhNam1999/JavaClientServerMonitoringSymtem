package Server;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Main {
    public static JFrame window;
    public static int port;
    public static JTextField jtextport;
    public static JButton jbutton;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        init();
    }

    public void init() {
        window = new JFrame("Server");
        window.setLayout(null);
        window.setBounds(200, 200, 450, 300);
        window.setResizable(false);

        JLabel label = new JLabel("Server");
        label.setBounds(180, 40, 80, 30);
        label.setFont(new Font("Serif", Font.PLAIN, 30));
        window.add(label);
        JLabel labelport = new JLabel("Port");
        labelport.setBounds(20, 110, 80, 30);
        labelport.setFont(new Font("Serif", Font.PLAIN, 24));
        window.add(labelport);
        jtextport = new JTextField("");
        jtextport.setSize(new Dimension(100, 50));
        jtextport.setBounds(80, 110, 200, 30);
        window.add(jtextport);
        jbutton = new JButton("start");
        jbutton.setBounds(300, 110, 100, 30);
        window.add(jbutton);
        myEvent();
        window.setVisible(true);
    }

    public void myEvent() {
        jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(jtextport.getText());
                if (port > 0 && port < 10000) {
                    Dashboard win = new Dashboard(port);
                    win.window.setVisible(true);
                    window.setVisible(false);
                    window.dispose();
                } else {
                    JOptionPane.showMessageDialog(window, "Port khong hop le!!");
                }
            }
        });
        jtextport.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume(); // if it's not a number, ignore the event
                }
            }
        });
    }
}
