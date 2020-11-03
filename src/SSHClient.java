import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class SSHClient {
    public static void main(String... args) throws Exception {

        /*
        List<String> list = Arrays.asList("kamailio", "squid");
        for (String service: list) {
            String command = format("systemctl status %s", service);
            String userName = "mcpttadm";
            String password = "Mcptt@dM$!202o";
            String connectionIP = "10.1.1.214";
            SSHManager instanceSSH = new SSHManager(userName, password, connectionIP);

            instanceSSH.connect();

            String cmdOutput = instanceSSH.sendCommand(command);

            instanceSSH.close();

            systemctlStatusParser parsedStatus = new systemctlStatusParser(cmdOutput, service);
            String status = parsedStatus.getActiveStatus();

            System.out.println(format("Command output %s = %s", command, cmdOutput));
            System.out.println(format("Status of %s is: %s", command, status));
        }

         */


        /*
        String userName = "mcpttadm";
        String password = "Mcptt@dM$!202o";
        String connectionIP = "10.1.1.214";
        String command = "echo 'Mcptt@dM$!202o' | sudo -S systemctl stop squid";

        System.out.println("Going to stop squid");
        SSHManager instanceSSH = new SSHManager(userName, password, connectionIP);
        instanceSSH.connect();
        String cmdOutput = instanceSSH.sendCommand(command);
        System.out.println(format("Command output %s = %s", command, cmdOutput));

        command = "systemctl status squid";
        cmdOutput = instanceSSH.sendCommand(command);
        systemctlStatusParser parsedStatus = new systemctlStatusParser(cmdOutput, "squid");
        String status = parsedStatus.getActiveStatus();
        System.out.println(format("Status of %s is: %s", command, status));

        System.out.println("\nGoing to start squid");
        command = "echo 'Mcptt@dM$!202o' | sudo -S systemctl start squid";
        cmdOutput = instanceSSH.sendCommand(command);
        System.out.println(format("Command output %s = %s", command, cmdOutput));

        command = "systemctl status squid";
        cmdOutput = instanceSSH.sendCommand(command);
        parsedStatus = new systemctlStatusParser(cmdOutput, "squid");
        status = parsedStatus.getActiveStatus();
        System.out.println(format("Status of %s is: %s", command, status));

        instanceSSH.close();
         */

        /*
        String ProxyABOXIP = "10.1.1.214";
        String ProxyBBOXIP = "10.1.1.188";

        HACli haCli = new HACli();

        if (haCli.getMemberStatus(ProxyABOXIP).contains("scope global nodad"))
            System.out.printf("%s is MASTER", ProxyABOXIP);
        else if (haCli.getMemberStatus(ProxyBBOXIP).contains("scope global nodad"))
            System.out.printf("%s is MASTER", ProxyBBOXIP);

        System.out.printf("squid status in %s: %s", ProxyABOXIP, haCli.getMemberServiceStatus(ProxyABOXIP, "squid"));
        System.out.printf("squid status in %s: %s", ProxyBBOXIP, haCli.getMemberServiceStatus(ProxyABOXIP, "squid"));

        System.out.printf("Stopping squid in %s with output: %s", ProxyABOXIP, haCli.stopMemberService(ProxyABOXIP, "squid"));
        System.out.printf("squid status in %s: %s", ProxyABOXIP, haCli.getMemberServiceStatus(ProxyABOXIP, "squid"));
        System.out.printf("squid status in %s: %s", ProxyBBOXIP, haCli.getMemberServiceStatus(ProxyBBOXIP, "squid"));

        System.out.printf("Starting squid in %s with output: %s", ProxyABOXIP, haCli.startMemberService(ProxyABOXIP, "squid"));
        System.out.printf("squid status in %s: %s", ProxyABOXIP, haCli.getMemberServiceStatus(ProxyABOXIP, "squid"));
        System.out.printf("squid status in %s: %s", ProxyBBOXIP, haCli.getMemberServiceStatus(ProxyABOXIP, "squid"));

        if (haCli.getMemberStatus(ProxyABOXIP).contains("scope global nodad"))
            System.out.printf("%s is MASTER", ProxyABOXIP);
        else if (haCli.getMemberStatus(ProxyBBOXIP).contains("scope global nodad"))
            System.out.printf("%s is MASTER", ProxyBBOXIP);

\\

        String ProxyABOXIP = "10.1.1.214";
        String ProxyBBOXIP = "10.1.1.188";

        HACli haCli = new HACli();

        String output = haCli.getMemberServiceStatus(ProxyABOXIP, "squid");
        Integer oldPid = haCli.getMemberServicePID(ProxyABOXIP, "squid");

        String result = haCli.restartMemberService(ProxyABOXIP, "squid", 5);

        output = haCli.getMemberServiceStatus(ProxyABOXIP, "squid");
        Integer newPid = haCli.getMemberServicePID(ProxyABOXIP, "squid");

         */
        String ProxyABOXIP = "10.1.1.214";
        String ProxyBBOXIP = "10.1.1.188";
        String PTTABOX70 = "10.1.1.206";
        String PTTBBOX70 = "10.1.1.187";

        String command = "nohup echo 'Mcptt@dM$!202o' | sudo -S sngrep -O /var/tmp/sngrep_output1.txt -N";
        command = "nohup /usr/bin/ping 127.0.0.1 > /var/tmp/ping_output.txt";
        String userName = "mcpttadm";
        String password = "Mcptt@dM$!202o";
        String connectionIP = "10.1.206";
        SSHManager instanceSSH = new SSHManager(userName, password, connectionIP);

        instanceSSH.connect();

        instanceSSH.sendCommandWithoutOutput(command);
        String output = instanceSSH.sendCommand("ps -ef | grep -i ping | grep -v grep");
        System.out.printf("Got this output: %s\n", output);
        output = instanceSSH.sendCommand("pkill ping");
        output = instanceSSH.sendCommand("ps -ef | grep -i ping | grep -v grep");
        System.out.printf("Got this output: %s\n", output);
        instanceSSH.close();


        HAClusterInformation ha = new HAClusterInformation(ProxyABOXIP, ProxyBBOXIP, PTTABOX70, PTTBBOX70);
        if (ha.verifyHAStatus()) System.out.print("HA is in good NICK\n");
        else System.exit(1);
        if (ha.retrieveProxyClusterServicesStatus()) System.out.print("All services running in all servers\n");
        else System.exit(2);

        System.out.println("\nBye bye");
    }
}
