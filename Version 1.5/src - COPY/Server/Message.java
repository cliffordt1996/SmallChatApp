
public class Message implements java.io.Serializable {

    private ClientForm clientTo, clientFrom;
    private byte[] clientText;

    private boolean isEncrypted;

    /*CONSTRUCTORS*************************************************************/
    public Message() {
        clientTo = new ClientForm();
        clientFrom = new ClientForm();
        clientText = "".getBytes();
        isEncrypted = false;
    }

    public Message(ClientForm clientFrom, String clientText) {
        this.clientFrom = clientFrom;
        this.clientText = clientText.getBytes();
        isEncrypted = false;
    }

    public Message(ClientForm clientFrom, String clientText, ClientForm clientTo) {
        this(clientFrom, clientText);
        this.clientTo = clientTo;
    }

    /*SETTERS******************************************************************/
    public void setClientFrom(ClientForm clientFrom) {
        this.clientFrom = clientFrom;
    }

    public void setClientText(String clientText) {
        this.clientText = clientText.getBytes();
    }

    public void setClientTo(ClientForm clientTo) {
        this.clientTo = clientTo;
    }

    public void setIsEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    /*GETTERS******************************************************************/
    public ClientForm getClientFrom() {
        return clientFrom;
    }

    public String getClientText() {
        return new String(clientText);
    }

    public ClientForm getClientTo() {
        return clientTo;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    /*UTILITY******************************************************************/
    public String getTextMessage() {
        return (clientFrom.getCustomName() + " >> " + clientTo.getCustomName()
                + " : " + new String(clientText));
    }

    public void encryptClientText(String encryptionKey) {
        TEA tea = new TEA(encryptionKey.getBytes());
        clientText = tea.encrypt(clientText);
        isEncrypted = true;
    }

    public void decryptClientText(String decryptionKey) {
        TEA tea = new TEA(decryptionKey.getBytes());
        clientText = tea.decrypt(clientText);
        isEncrypted = false;
    }

    /*PRIVATE******************************************************************/
}
