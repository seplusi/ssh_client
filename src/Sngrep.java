import static java.lang.String.format;

public class Sngrep {
    private SSHManager instanceSSH = null;
    private String sngrepOutputFile = "/var/tmp/sngrep_output_txt";

    public Sngrep(String ipAddress, String user, String password) {
        SSHManager instanceSSH = new SSHManager(user, password, ipAddress);
    }

    public boolean cleanUp() {
        instanceSSH.sendCommand("pkill sngrep");
        instanceSSH.sendCommand(format("echo 'Mcptt@dM$!202o' | sudo -S rm %s", sngrepOutputFile));

        String checkRunningSngreps = instanceSSH.sendCommand("ps -ef | grep -i sngrep | grep -v grep");
        String CheckSngrepOutputFiles = instanceSSH.sendCommand(format("echo 'Mcptt@dM$!202o' | sudo -S ls %s", sngrepOutputFile));
    }
}
