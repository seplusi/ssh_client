import java.util.Arrays;
import java.util.List;

public class ProxyServer {
    private String ipAddress;
    private String status = "";
    private String squidStatus = "";
    private String kamailioStatus = "";
    private HACli haCli = new HACli();

    public ProxyServer(String ipAddress) throws Exception {
        this.ipAddress = ipAddress;
        status = haCli.getProxyStatus(ipAddress);
        squidStatus = haCli.getMemberServiceStatus(ipAddress, "squid");
        kamailioStatus = haCli.getMemberServiceStatus(ipAddress, "kamailio");
    }

    public String getProxyStatus() {
        return status;
    }

    public List<String> getServerServices() {
        return Arrays.asList(squidStatus, kamailioStatus);
    }
}