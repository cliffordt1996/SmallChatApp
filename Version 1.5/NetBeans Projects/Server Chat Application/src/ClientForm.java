
/**
 * ClientForms are used to summarize Client details mainly for identification
 * purposes across a network.
 */
public class ClientForm implements java.io.Serializable {

    private String customName, ipAddress;

    /*CONSTRUCTORS*************************************************************/
    public ClientForm() {
        customName = "";
        ipAddress = "";
    }

    public ClientForm(String customName, String ipAddress) {
        this.customName = customName;
        this.ipAddress = ipAddress;
    }

    /*SETTERS******************************************************************/
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /*GETTERS******************************************************************/
    public String getCustomName() {
        return customName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    /*UTILITY******************************************************************/
    @Override
    public String toString() {
        return customName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ClientForm) {
            return (((ClientForm) o).getCustomName().equals(customName)
                    && ((ClientForm) o).getIpAddress().equals(ipAddress));
        } else {
            return false;
        }
    }

    /*PRIVATE******************************************************************/
}
