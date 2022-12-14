import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WatchFolder implements Runnable {
    public static WatchService watchService;
    private Socket s;

    public WatchFolder(Socket s) {
        this.s = s;
    }

    public void dispose() throws IOException {
        watchService.close();
    }

    @Override
    public void run() {
        try {

            System.out.println("Watching directory for changes");
            // STEP1: Create a watch service
            watchService = FileSystems.getDefault().newWatchService();

            // STEP2: Get the path of the directory which you want to monitor.
            Path directory = Path.of(ClientHandler.pathDirectory);

            // STEP3: Register the directory with the watch service
            WatchKey watchKey = directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            // STEP4: Poll for events
            while (true) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {

                    // STEP5: Get file name from even context
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

                    Path fileName = pathEvent.context();

                    // STEP6: Check type of event.
                    WatchEvent.Kind<?> kind = event.kind();

                    // STEP7: Perform necessary action with the event
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(date), "Created",
                                ClientHandler.nameClient,
                                "A new file is created : " + fileName };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Created" + "," +
                                ClientHandler.nameClient + "," +
                                "A new file is created : " + fileName + "}";

                        ClientHandler.jobsModel.addRow(obj);
                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                        new ClientSend(s, ClientHandler.nameClient, "10", "A new file is created : " + fileName,
                                ClientHandler.pathDirectory);
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {

                        System.out.println("A file has been deleted: " + fileName);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(date), "Deleted",
                                ClientHandler.nameClient,
                                "A file has been deleted : " + fileName };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Deleted" + "," +
                                ClientHandler.nameClient + "," +
                                "A file has been deleted : " + fileName + "}";

                        ClientHandler.jobsModel.addRow(obj);
                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                        new ClientSend(s, ClientHandler.nameClient, "11", "A file has been deleted : " + fileName,
                                ClientHandler.pathDirectory);
                    }
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

                        System.out.println("A file has been modified: " + fileName);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(date), "Modified",
                                ClientHandler.nameClient,
                                "A file has been modified : " + fileName };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Modified" + "," +
                                ClientHandler.nameClient + "," +
                                "A file has been modified : " + fileName + "}";

                        ClientHandler.jobsModel.addRow(obj);
                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                        WriteFile wr = new WriteFile();
                        wr.writeFile(String.valueOf(data), ClientHandler.pathDirectory, ClientHandler.nameClient);
                        new ClientSend(s, ClientHandler.nameClient, "12", "A file has been modified : " + fileName,
                                ClientHandler.pathDirectory);
                    }

                }

                // STEP8: Reset the watch key everytime for continuing to use it for further
                // event polling
                boolean valid = watchKey.reset();
                if (!valid) {
                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
