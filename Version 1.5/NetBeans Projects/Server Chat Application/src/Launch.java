// Author       : Thomas M. Clifford
// Date Created : 10/01/2016
// Last Modified: 11/07/2016
// About        : A networking chat application.

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

public class Launch {

    public static final int DEFAULT_PORT = 60100;
    public static final ClientForm DEFAULT_SENDTO = new ClientForm("All", "All");

    private static ServerApp app;

    public static void main(String[] args) {
        app = new ServerApp();
        app.setVisible(true);
        try { // Setup the server...
            ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
            app.println(
                    "Starting server at: " + new Date() + '\n'
                    + "Server IP: "
                    + serverSocket.getInetAddress().getHostAddress()
                    + ':' + serverSocket.getLocalPort());
            do {
                try {
                    // Listen for new clients; put each new client on its own 
                    // thread for handling and register them to the server.
                    while (app != null) {
                        Client client = new Client(serverSocket.accept());
                        client.setCustomName(
                                ((ClientForm) client.readObject()).getCustomName());
                        app.sendAllClientForm(client.asForm()); // Notify other users of new client.
                        app.add(client);
                        app.println("Client detected. Attempting to setup thread.");
                        new Thread(new HandleAClient(client)).start();
                    }
                } catch (IOException ex) {
                    app.println("Failed to start thread for client.");
                    ex.printStackTrace();
                }
            } while (app != null);
        } catch (IOException ex) {
            System.err.println("Failed to setup server socket.");
            System.exit(1);
        }
    }

    public static class HandleAClient implements Runnable {

        private Client client;

        public HandleAClient(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // Thread for client successfully starts running; notify server.
                app.println(
                        "Thread started for client:\n\t"
                        + "IP Address: "
                        + client.getSocket().getInetAddress().getHostAddress()
                        + " | Name: "
                        + client.getCustomName());

                // Notify client of all other currently connected users.
                ClientForm[] currForms = app.getListasClientForms();
                for (ClientForm c : currForms) {
                    if (!(c.equals(client.asForm()))) {
                        client.writeObject(c);
                    }
                }

                // Begin handling incoming data from client.
                while (client.getSocket().isConnected() && app.isClientListed(client)) {
                    Object o = client.readObject();
                    if (o instanceof Message) {
                        Message message = (Message) o;
                        if (app.isClientListed(client)) { // Check if status changed during wait.
                            if (message.getClientTo().equals(DEFAULT_SENDTO)) { // Message is for all clients.
                                app.sendAllMessage(message);
                                app.println(
                                        "Sent all clients the following message:\n\t"
                                        + message.getTextMessage());
                            } else { // Message has a specific target.
                                app.sendMessage(client.asForm(), message); // Give sender a copy of the message.
                                app.sendMessage(message.getClientTo(), message);
                                app.println(
                                        "Sent " + message.getClientTo().getCustomName()
                                        + " the following message from "
                                        + message.getClientFrom().getCustomName()
                                        + ":\n\t"
                                        + message.getTextMessage());
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                // Losing the client; notify server and other clients; perform cleanup.
                app.println(
                        "Lost client:\n\t"
                        + "IP Address: "
                        + client.getSocket().getInetAddress().getHostAddress()
                        + " | Name: "
                        + client.getCustomName()
                        + "\nPerforming cleanup...");
                app.remove(client);
                client.closeStreams(); // In case streams are open.
                app.sendAllClientForm(client.asForm()); // For client-side cleanup.
                app.println("Cleanup complete.");
            }
        }
    }
}
