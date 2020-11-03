import java.util.HashSet;

public class HAClusterInformation {
    String proxyAIP = "";
    String proxyBIP = "";
    String pttAIP = "";
    String pttBIP = "";
    ProxyServer proxyA = null;
    ProxyServer proxyB = null;
    PTTServer pttA = null;
    PTTServer pttB = null;

    public HAClusterInformation(String proxyAIP, String proxyBIP, String pttAIP, String pttBIP) throws Exception {
        this.proxyAIP = proxyAIP;
        this.proxyBIP = proxyBIP;
        this.pttAIP = pttAIP;
        this.pttBIP = pttBIP;

        proxyA = new ProxyServer(proxyAIP);
        proxyB = new ProxyServer(proxyBIP);
        pttA = new PTTServer(pttAIP);
        pttB = new PTTServer(pttBIP);
    }

    public boolean verifyHAStatus() {
        boolean verifyProxyCluster = false;
        boolean verifyPTTCluster = false;

        if ((proxyA.getProxyStatus().equals("master") && (proxyB.getProxyStatus().equals("backup"))) || ((proxyA.getProxyStatus().equals("backup")) && (proxyB.getProxyStatus().equals("master")))) {
            System.out.printf("%s is %s and %s is %s\n", proxyAIP, proxyA.getProxyStatus(), proxyBIP, proxyB.getProxyStatus());
            verifyProxyCluster = true;
        }

        if ((pttA.getPTTStatus().equals("master") && pttB.getPTTStatus().equals("backup")) || (pttA.getPTTStatus().equals("backup") && pttB.getPTTStatus().equals("master"))) {
            System.out.printf("%s is %s and %s is %s\n", pttAIP, pttA.getPTTStatus(), pttBIP, pttB.getPTTStatus());
            verifyPTTCluster = true;
        }

        return verifyPTTCluster & verifyProxyCluster;
    }

    public boolean retrieveProxyClusterServicesStatus() {
        boolean proxyAServicesRunning =  new HashSet<String> (proxyA.getServerServices()).size() == 1 && proxyA.getServerServices().get(0).equals("active (running)");
        boolean proxyBServicesRunning =  new HashSet<String> (proxyB.getServerServices()).size() == 1 && proxyB.getServerServices().get(0).equals("active (running)");
        boolean pttAServicesRunning =  new HashSet<String> (pttA.getServerServices()).size() == 1 && pttA.getServerServices().get(0).equals("active (running)");
        boolean pttBServicesRunning =  new HashSet<String> (pttB.getServerServices()).size() == 1 && pttB.getServerServices().get(0).equals("active (running)");

        return proxyAServicesRunning & proxyBServicesRunning & pttAServicesRunning & pttBServicesRunning;
    }

}

