
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

public class ClientApp {

    public static final String DEFAULT_TITLE = "Client";

    public static final ClientForm DEFAULT_SENDTO = new ClientForm("All", "All");

    public static final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);

    private static final Color[] COLOR_OPTION = {
        Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE,
        Color.PINK, Color.RED, Color.WHITE, Color.YELLOW
    };

    public static final int ENCRYPTIONKEY_LENGTH = 16;
    public static final int MAX_NAME_LENGTH = 24;

    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 500;

    public final Sound gotMessage
            = new Sound(this.getClass().getResource("/Sounds/gotMessage.wav"));

    public final Sound userConnected
            = new Sound(this.getClass().getResource("/Sounds/userConnected.wav"));

    public final Sound userDisconnected
            = new Sound(this.getClass().getResource("/Sounds/userDisconnected.wav"));

    public final Image titleIcon
            = new ImageIcon(this.getClass().getResource("/Images/Icon.png")).getImage();

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JScrollPane textScrollPane;
    private JLabel sendIndicator;
    private JComboBox sendTo;

    private boolean encryption;
    private boolean autoScroll;
    private boolean userNotifications;
    private boolean messageNotifications;

    private Client client;
    private ArrayList<ClientForm> clientList;

    /*CONSTRUCTORS*************************************************************/
    public ClientApp() {
        // Initialize attributes...
        encryption = autoScroll = false;
        userNotifications = messageNotifications = true;

        client = new Client();
        clientList = new ArrayList<>();
        clientList.add(DEFAULT_SENDTO);

        // Initialize components...
        textArea = new JTextArea();
        textField = new JTextField("");
        sendIndicator = new JLabel(" << ");
        sendTo = new JComboBox(clientList.toArray());
        JPanel centerPane = new JPanel(new GridLayout());
        JPanel bottomPane = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        frame = new JFrame();

        // Setup menu...
        JMenu menuSettings = new JMenu("Settings");
        JMenu textColorOption = new JMenu("Text Color");

        JMenuItem[] textColors = new JMenuItem[COLOR_OPTION.length];
        for (int i = 0; i < textColors.length; i++) {
            textColors[i] = new JMenuItem();
            textColors[i].setBackground(COLOR_OPTION[i]);
            textColors[i].addActionListener(e -> {
                Color color = ((JMenuItem) e.getSource()).getBackground();
                textArea.setForeground(color);
                textField.setForeground(color);
                sendIndicator.setForeground(color);
                sendTo.setForeground(color);
            });
        }
        for (JMenuItem color : textColors) {
            textColorOption.add(color);
        }
        menuSettings.add(textColorOption); // Add color options to settings.

        JCheckBox autoScrollOption = new JCheckBox("Auto Scroll", autoScroll);
        autoScrollOption.addChangeListener((ChangeEvent e) -> {
            autoScroll = !autoScroll;
        });
        menuSettings.add(autoScrollOption); // Add auto scroll option to settings.

        JCheckBox userNotificationsOption = new JCheckBox("User Notifications", userNotifications);
        userNotificationsOption.addChangeListener((ChangeEvent e) -> {
            userNotifications = !userNotifications;
        });
        menuSettings.add(userNotificationsOption); // Add user notifications option to settings.

        JCheckBox messageNotificationsOption = new JCheckBox("Message Notifications", messageNotifications);
        messageNotificationsOption.addChangeListener((ChangeEvent e) -> {
            messageNotifications = !messageNotifications;
        });
        menuSettings.add(messageNotificationsOption); // Add message notifications option to settings.

        JMenuItem changeEncryptionKey = new JMenuItem("Change Key");
        changeEncryptionKey.addActionListener(e -> {
            String key;
            do { // Get a valid encryption key.
                key = getEncryptionKey().trim();
            } while (key.getBytes().length < ENCRYPTIONKEY_LENGTH);
            client.setEncryptionKey(key);
        });
        menuSettings.add(changeEncryptionKey); // Add change key option to settings.

        JMenuItem clearTextArea = new JMenuItem("Clear Screen");
        clearTextArea.addActionListener(e -> {
            textArea.setText("");
        });
        menuSettings.add(clearTextArea); // Add clear screen option to settings.

        menuSettings.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            frame.dispose();
            client.closeStreams();
            System.exit(0);
        });
        menuSettings.add(exit); // Add exit option to settings.

        JCheckBox encryptionOption = new JCheckBox("Encryption", encryption);
        encryptionOption.addChangeListener((ChangeEvent e) -> {
            encryption = !encryption;
        });

        menuBar.add(menuSettings); // Add settings menu to menu bar.
        menuBar.add(encryptionOption); // Add encryption option to menu bar.

        // Setup text area...
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setFont(font);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setAutoscrolls(true);
        textArea.setEditable(false); // text area complete.

        // Setup scroll pane...
        textScrollPane = new JScrollPane(textArea);

        // Setup center panel...
        centerPane.add(textScrollPane);

        // Setup text field...
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setBackground(Color.BLACK);
        textField.setForeground(Color.GREEN);
        textField.setFont(font);
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !textField.getText().equals("") && client.isConnected()) {
                    Message message = new Message(
                            client.asForm(),
                            textField.getText().trim(),
                            (ClientForm) sendTo.getSelectedItem());

                    if (encryption) {
                        message.encryptClientText(client.getEncryptionKey());
                    }

                    client.writeObject(message);
                    textField.setText("");
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    frame.dispose();
                    client.closeStreams();
                    System.exit(0);
                }
            }
        });

        // Setup send label...
        sendIndicator.setBorder(BorderFactory.createEmptyBorder());
        sendIndicator.setOpaque(true);
        sendIndicator.setBackground(Color.BLACK);
        sendIndicator.setForeground(Color.GREEN);
        sendIndicator.setFont(font);

        // Setup combo box...
        sendTo.setEditable(false);
        sendTo.setBorder(BorderFactory.createEmptyBorder());
        sendTo.setBackground(Color.BLACK);
        sendTo.setForeground(Color.GREEN);
        sendTo.setFont(font);
        sendTo.setPreferredSize(new Dimension(120, 10));

        // Setup bottom panel...
        bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
        bottomPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        bottomPane.add(sendTo);
        bottomPane.add(sendIndicator);
        bottomPane.add(textField);

        // Setup frame...
        frame.setLayout(new BorderLayout());
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(titleIcon);
        frame.setTitle(DEFAULT_TITLE);

        // Set/Add components to frame...
        frame.setJMenuBar(menuBar);
        frame.add(centerPane, BorderLayout.CENTER);
        frame.add(bottomPane, BorderLayout.SOUTH);
    }

    public ClientApp(Client client) {
        this();

        this.client = client;
        frame.setTitle(
                DEFAULT_TITLE + " Connected to Host: "
                + this.client.getInetAddress().getHostAddress());
    }

    /*SETTERS******************************************************************/
    public void setClient(Client client) {
        this.client = client;
    }

    /*GETTERS******************************************************************/
    public Client getClient() {
        return client;
    }

    /*UTILITY******************************************************************/
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public class ObjectCollector implements Runnable {

        @Override
        public void run() { // Handle data sent in from the server.
            try {
                while (client.isConnected()) {
                    Object o = client.readObject();
                    if (o instanceof Message) {
                        Message message = (Message) o;
                        if (!message.isEncrypted()) {
                            textArea.append(message.getTextMessage() + '\n');
                        } else { // Attempt decryption with current key.
                            message.decryptClientText(client.getEncryptionKey());
                            textArea.append(message.getTextMessage() + '\n');
                        } // Play a sound if message is from another user.
                        if (!message.getClientFrom().equals(client.asForm())
                                && messageNotifications) {
                            gotMessage.play();
                        }
                        if (autoScroll) { // Adjust display according to settings.
                            JScrollBar sb = textScrollPane.getVerticalScrollBar();
                            sb.setValue(sb.getMaximum());
                        }
                    } else if (o instanceof ClientForm) {
                        ClientForm cf = (ClientForm) o;
                        if (clientList.contains(cf)) { // User left.
                            clientList.remove(cf);
                            sendTo.removeItem(cf);
                            if (userNotifications) {
                                userDisconnected.play();
                            }
                        } else { // User joined
                            clientList.add(cf);
                            sendTo.addItem(cf);
                            if (userNotifications) {
                                userConnected.play();
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                textArea.append("Disconnected from host.");
                client.closeStreams();
            }
        }
    }

    public static String getIP() {
        Object o = JOptionPane.showInputDialog(
                null, "Host Address:", DEFAULT_TITLE, JOptionPane.QUESTION_MESSAGE);
        if (!o.equals(JOptionPane.CANCEL_OPTION)) {
            return (String) o;
        } else {
            return "";
        }
    }

    public static String getUserName() {
        Object o = JOptionPane.showInputDialog(
                null, "Name:", DEFAULT_TITLE, JOptionPane.QUESTION_MESSAGE);
        if (!o.equals(JOptionPane.CANCEL_OPTION)) {
            return (String) o;
        } else {
            return "";
        }
    }

    public static String getEncryptionKey() {
        Object o = JOptionPane.showInputDialog(
                null, "16-Byte Encryption Key:", DEFAULT_TITLE, JOptionPane.QUESTION_MESSAGE);
        if (!o.equals(JOptionPane.CANCEL_OPTION)) {
            return (String) o;
        } else {
            return "";
        }
    }

    public static void notifyFailedToConnect() {
        JOptionPane.showMessageDialog(
                null, "Failed to connect to server.", DEFAULT_TITLE, JOptionPane.ERROR_MESSAGE);
    }
    /*PRIVATE******************************************************************/
}
