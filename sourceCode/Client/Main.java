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
    public static JTextField jtextip;
    public static JTextField jtextname;
    public static JButton jbutton;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        init();
    }

    public void init() {
        window = new JFrame("Client");
        window.setLayout(null);
        window.setBounds(300, 200, 330, 300);
        window.setResizable(false);

        JLabel label = new JLabel("Client");
        label.setBounds(125, 20, 80, 30);
        label.setFont(new Font("Serif", Font.PLAIN, 30));
        window.add(label);

        JLabel labelip = new JLabel("IP");
        labelip.setBounds(20, 80, 80, 30);
        labelip.setFont(new Font("Serif", Font.PLAIN, 24));
        window.add(labelip);
        jtextip = new JTextField("127.0.0.1");
        jtextip.setSize(new Dimension(100, 50));
        jtextip.setBounds(80, 80, 200, 30);
        window.add(jtextip);

        JLabel labelport = new JLabel("Port");
        labelport.setBounds(20, 120, 80, 30);
        labelport.setFont(new Font("Serif", Font.PLAIN, 24));
        window.add(labelport);
        jtextport = new JTextField("8888");
        jtextport.setSize(new Dimension(100, 50));
        jtextport.setBounds(80, 120, 200, 30);
        window.add(jtextport);

        JLabel labelname = new JLabel("Name");
        labelname.setBounds(20, 160, 80, 30);
        labelname.setFont(new Font("Serif", Font.PLAIN, 24));
        window.add(labelname);
        jtextname = new JTextField("");
        jtextname.setSize(new Dimension(100, 50));
        jtextname.setBounds(80, 160, 200, 30);
        window.add(jtextname);

        jbutton = new JButton("Connect");
        jbutton.setBounds(100, 200, 100, 30);
        window.add(jbutton);
        myEvent();
        window.setVisible(true);
    }

    public void myEvent() {
        jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int port = Integer.parseInt(jtextport.getText());
                String ip = jtextip.getText();
                String name = jtextname.getText();

                if (port > 0 && port < 10000 && ip.length() != 0 && name.length() != 0) {
                    ClientHandler win = new ClientHandler(ip, port, name);
                    win.window.setVisible(true);
                    window.setVisible(false);
                    window.dispose();
                } else {
                    JOptionPane.showMessageDialog(window, "Thông tin không hợp lệ !!");
                }
            }
        });

        jtextport.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });
    }
}
