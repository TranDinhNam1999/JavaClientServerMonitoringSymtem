import javax.swing.*;

public class TableWatchFolder {
    JFrame f;

    TableWatchFolder() {
        f = new JFrame();

        String data[][] = { { "101", "Tran Van Minh", "6000" },
                { "102", "Phan Van Tai", "8000" },
                { "101", "Do Cao Hoc", "7000" } };
        String column[] = { "ID", "NAME", "SALARY" };

        JTable jt = new JTable(data, column);
        jt.setBounds(30, 40, 200, 300);

        JScrollPane sp = new JScrollPane(jt);
        sp.setBounds(10, 10, 100, 100);
        f.add(sp);

        f.setSize(500, 300);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        new TableWatchFolder();
    }
}
