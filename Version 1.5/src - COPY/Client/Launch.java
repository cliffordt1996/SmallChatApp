// Author       : Thomas M. Clifford
// Date Created : 10/01/2016
// Last Modified: 11/07/2016
// About        : A networking chat application.

import java.io.IOException;

public class Launch {

    private static final int DEFAULT_PORT = 60100;

    private static ClientApp app;

    public static void main(String[] args) {
        String address = ClientApp.getIP().trim(), name, key; // Eventually, make a single dialog...

        do { // Get a valid user name.
            name = ClientApp.getUserName().trim();
        } while (name.equalsIgnoreCase(ClientApp.DEFAULT_SENDTO.getCustomName())
                || name.equals("")
                || name.length() > ClientApp.MAX_NAME_LENGTH);

        do { // Get a valid encryption key.
            key = ClientApp.getEncryptionKey().trim();
        } while (key.getBytes().length < ClientApp.ENCRYPTIONKEY_LENGTH);

        try { // Try to setup client using the above input.
            app = new ClientApp(new Client(address, DEFAULT_PORT, name, key));
            app.getClient().writeObject(app.getClient().asForm());
            app.setVisible(true);
            new Thread(app.new ObjectCollector()).start();
        } catch (IOException ex) { // Could not reach host.
            ClientApp.notifyFailedToConnect();
            System.err.println("Failed to connect to host using specified IP");
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
