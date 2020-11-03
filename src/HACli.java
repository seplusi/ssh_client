import java.util.HashMap;

import static java.lang.String.format;

public class HACli {
    private final String userName = "mcpttadm";
    private final String password = "Mcptt@dM$!202o";
    private final String stoppedStatusMsg = "inactive (dead)";
    private final String startedStatusMsg = "active (running)";

    public String getProxyStatus(String ipAddress) throws Exception {
        String command = "/usr/sbin/ip -6 addr | grep 'scope global nodad' ";
        String keepAlivedStatusCmd = "echo 'Mcptt@dM$!202o' | sudo -S cat /etc/keepalived/STATUS";
        System.out.printf("\nCheck connection in %s%n", ipAddress);

        String ipv6CmdOutput = runCmdThroughSSH(ipAddress, command);
        String keepAlivedCmdOutput = runCmdThroughSSH(ipAddress, keepAlivedStatusCmd);

        if (ipv6CmdOutput.contains("scope global nodad") && keepAlivedCmdOutput.equals("master\n"))
            return "master";
        else if (!ipv6CmdOutput.contains("scope global nodad") && keepAlivedCmdOutput.equals("backup\n"))
            return "backup";
        else
            throw new Exception(format("Proxy server %s isn't in a good status: KEEPALIVED = %s and IPV6 = %s.", ipAddress, keepAlivedCmdOutput, ipv6CmdOutput));
    }

    public String getPTTStatus(String ipAddress) throws Exception {
        String statusCmd = "echo 'Mcptt@dM$!202o' | sudo -S cat /etc/gnkgnk/georedundancy/status";
        String howAmICmd = "echo 'Mcptt@dM$!202o' | sudo -S cat /etc/gnkgnk/georedundancy/howAmI";
        System.out.printf("\nCheck connection in %s%n", ipAddress);

        String statusOutput = runCmdThroughSSH(ipAddress, statusCmd);
        String howAmIOutput = runCmdThroughSSH(ipAddress, howAmICmd);

        if (statusOutput.equals("master\n") && howAmIOutput.equals("OK\n"))
            return "master";
        else if (statusOutput.equals("backup\n") && howAmIOutput.equals("OK\n"))
            return "backup";
        else
            throw new Exception(format("PTT server %s isn't in a good status: STATUS = %s and HOWAMI = %s.", ipAddress, statusOutput, howAmIOutput));
    }

    public String getMemberServiceStatus(String ipAddress, String serviceName) throws Exception {
        String command = format("systemctl status %s", serviceName);
        System.out.printf("\nCheck status of %s. ", serviceName);

        String cmdOutput = runCmdThroughSSH(ipAddress, command);

        systemctlStatusParser parsedStatus = new systemctlStatusParser(cmdOutput, serviceName);
        String status = parsedStatus.getActiveStatus();
        System.out.printf("Status of %s is: %s%n", command, status);

        return status;
    }

    public Integer getMemberServicePID(String ipAddress, String serviceName) throws Exception {
        String command = format("systemctl status %s", serviceName);
        System.out.printf("\nCheck status of %s. ", serviceName);

        String cmdOutput = runCmdThroughSSH(ipAddress, command);

        systemctlStatusParser parsedStatus = new systemctlStatusParser(cmdOutput, serviceName);
        Integer pidNumber = parsedStatus.getMainPID();
        System.out.printf("PID of %s is: %d%n", command, pidNumber);

        return pidNumber;
    }

    public String stopMemberService(String ipAddress, String serviceName) {
        return runSUSystemctlCmd(ipAddress, serviceName, "stop");
    }

    public String stopMemberService(String ipAddress, String serviceName, int timeout) throws Exception {
        String status = "fail";
        runSUSystemctlCmd(ipAddress, serviceName, "stop");
        for (int counter = 0; counter <= timeout; counter++) {
            status = getMemberServiceStatus(ipAddress, serviceName);
            if (status.equals(stoppedStatusMsg)) break;
            Thread.sleep(1000);
        }

        return status;
    }

    public String startMemberService(String ipAddress, String serviceName) {
        return runSUSystemctlCmd(ipAddress, serviceName, "start");
    }

    public String restartMemberService(String ipAddress, String serviceName) {
        return runSUSystemctlCmd(ipAddress, serviceName, "restart");
    }

    public String restartMemberService(String ipAddress, String serviceName, Integer timeout) throws Exception {
        Integer oldPid = getMemberServicePID(ipAddress, "squid");
        runSUSystemctlCmd(ipAddress, serviceName, "restart");

        for (int counter = 0; counter <=timeout; counter++) {
            if (getMemberServicePID(ipAddress, "squid") != oldPid && getMemberServiceStatus(ipAddress, serviceName).contains(startedStatusMsg)) {
                return startedStatusMsg;
            }
            Thread.sleep(1000);
        }
        throw new Exception(format("Expected %s with status %s but got %s", serviceName, startedStatusMsg, getMemberServiceStatus(ipAddress, serviceName)));
    }

    /*
    public boolean waitForIPFailover(String newMaster, String newBackup, int timeout) throws InterruptedException {
        for (int counter = 0; counter <= timeout; counter = counter + 5) {
            if (getProxyStatus(newMaster).contains("scope global nodad") && getProxyStatus(newBackup).isEmpty()) {
                System.out.printf("%s is MASTER. %s is BACKUP", newMaster, newMaster);
                return true;
            }
            System.out.printf("%s is still MASTER. %s is still BACKUP", newBackup, newMaster);
            Thread.sleep(5000);
        }
        System.out.printf("TIMEOUT after %d seconds. %s is still MASTER. %s is still BACKUP", timeout, newBackup, newMaster);
        return false;
    }

    public HashMap getHAStatus(String pttA, String pttB, String proxyA, String proxyB) {

        HashMap systemStatus = new HashMap();

        if (getProxyStatus(proxyA).contains("scope global nodad") && getProxyStatus(proxyB).isEmpty()) {
            systemStatus.put("MASTER", proxyA);
            systemStatus.put("BACKUP", proxyB);
        } else if (getProxyStatus(proxyB).contains("scope global nodad") && getProxyStatus(proxyA).isEmpty()) {
            systemStatus.put("MASTER", proxyB);
            systemStatus.put("BACKUP", proxyA);
        }

        return systemStatus;
    }

     */

    public void killServiceProcess(String ipAddress, String service, Integer timeout) throws Exception {
        boolean success = false;
        Integer servicePid = getMemberServicePID(ipAddress, service);

        String command = format("echo 'Mcptt@dM$!202o' | sudo -S kill -9 %d", servicePid);
        System.out.printf("Killing %s with PID %d", service, servicePid);
        runCmdThroughSSH(ipAddress, command);

        for (int counter = 0; counter <= timeout; counter++) {
            if (!getMemberServiceStatus(ipAddress, service).equals(startedStatusMsg)) {
                System.out.printf("Service %s in %s is no longer %s", service, ipAddress, startedStatusMsg);
                success = true;
                break;
            }
            Thread.sleep(1000);
        }
        if (!success)
            throw new Exception(format("Got status %s for service %s in host %s", startedStatusMsg, service, ipAddress));
    }

    private String runSUSystemctlCmd(String ipAddress, String serviceName, String subCmd) {
        String command = format("echo 'Mcptt@dM$!202o' | sudo -S systemctl %s %s", subCmd, serviceName);
        System.out.printf("\n%s %s in %s", subCmd, serviceName, ipAddress);

        String cmdOutput = runCmdThroughSSH(ipAddress, command);
        System.out.printf("Command output %s = %s%n", command, cmdOutput);

        return cmdOutput;
    }

    private String runCmdThroughSSH(String ipAddress, String command) {
        SSHManager instanceSSH = new SSHManager(userName, password, ipAddress);
        instanceSSH.connect();
        String cmdOutput = instanceSSH.sendCommand(command);
        instanceSSH.close();

        return cmdOutput;
    }
}
