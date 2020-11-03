import java.util.Arrays;
import java.util.List;

public class PTTServer {
    private String ipAddress;
    private String status = "";
    private String dsopocserverStatus = "";
    private String rxconnectorStatus = "";
    private String locserverStatus = "";
    private String mysqlStatus = "";
    private HACli haCli = new HACli();

    public PTTServer(String ipAddress) throws Exception {
        this.ipAddress = ipAddress;
//        status = haCli.getPTTStatus(ipAddress);
        if (ipAddress.equals("10.1.1.206")) status = "backup";
        else status = "master";
        dsopocserverStatus = haCli.getMemberServiceStatus(ipAddress, "dsopocserver");
        rxconnectorStatus = haCli.getMemberServiceStatus(ipAddress, "rxconnector");
        locserverStatus = haCli.getMemberServiceStatus(ipAddress, "locserver");
        mysqlStatus = haCli.getMemberServiceStatus(ipAddress, "mariadb");
    }

    public String getPTTStatus() {
        return status;
    }

    public List<String> getServerServices() {
        return Arrays.asList(dsopocserverStatus, rxconnectorStatus, locserverStatus, mysqlStatus);
    }
}
