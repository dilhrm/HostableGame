
import networking.Client;
import networking.Server;

import javax.swing.*;
import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        int reply = JOptionPane.showConfirmDialog(null, "Will this computer be a host?", "Host", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            Server server = new Server();
            server.run();
        }
        new Client();
        //for using separate computers, you can remove the 2nd client
        new Client();
    }
}
