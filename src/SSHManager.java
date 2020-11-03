import com.jcraft.jsch.*;

import java.io.*;

public class SSHManager {
    private final JSch jschSSHChannel;
    private final String strUserName;
    private final String strConnectionIP;
    private final String strPassword;
    private Session sesConnection;
    private final int intTimeOut;

    public SSHManager(String userName, String password, String connectionIP)
    {
        jschSSHChannel  = new JSch();
        strUserName = userName;
        strPassword = password;
        strConnectionIP = connectionIP;
        intTimeOut = 60000;
    }

    public String connect()
    {
        String errorMessage = null;

        try
        {
            int intConnectionPort = 22;
            sesConnection = jschSSHChannel.getSession(strUserName, strConnectionIP, intConnectionPort);
            sesConnection.setPassword(strPassword);
            sesConnection.setConfig("StrictHostKeyChecking", "no");
            sesConnection.connect(intTimeOut);
        }
        catch(JSchException jschX)
        {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }

    public String sendCommandOldBck(String command)
    {
        StringBuilder outputBuffer = new StringBuilder();

        try
        {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while(readByte != 0xffffffff)
            {
                outputBuffer.append((char)readByte);
                readByte = commandOutput.read();
            }

            channel.disconnect();
        }
        catch(IOException | JSchException ioX)
        {
            return null;
        }

        return outputBuffer.toString();
    }

    public String sendCommandOldBck2()
    {
        StringBuilder outputBuffer = new StringBuilder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try
        {
            ChannelShell channel = (ChannelShell) sesConnection.openChannel("shell");
            channel.setOutputStream(outputStream);
            PrintStream stream = new PrintStream(channel.getOutputStream());
            channel.connect();

            stream.println("echo 'Mcptt@dM$!202o' | sudo -S systemctl stop squid");
            stream.flush();
            waitForPrompt(outputStream);

            stream.println("Mcptt@dM$!202o\n");
            stream.flush();
            waitForPrompt(outputStream);

//            stream.println("ls");
//            stream.flush();
//            waitForPrompt(outputStream);

            channel.disconnect();
        }
        catch(IOException | JSchException | InterruptedException ioX)
        {
            return null;
        }

        return outputBuffer.toString();
    }

    private void waitForPrompt(ByteArrayOutputStream outputStream) throws InterruptedException {
        int retries = 5;
        for (int x = 1; x < retries; x++) {
            Thread.sleep(1000);
            if (outputStream.toString().indexOf("$") > 0) {
                System.out.print(outputStream.toString());
                outputStream.reset();
                return;
            }
        }
    }

    public String sendCommand(String command)
    {
        StringBuilder outputBuffer = new StringBuilder();

        try
        {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream(), "UTF-8"));
            channel.connect();

            String line = "";
            while ((line = reader.readLine()) != null) {
                outputBuffer.append(line + "\n");
            }

            channel.disconnect();
        }
        catch(IOException | JSchException ioX)
        {
            return null;
        }

        return outputBuffer.toString();
    }

    public String sendCommandWithoutOutput(String command) throws JSchException, IOException {
        StringBuilder outputBuffer = new StringBuilder();

        Channel channel = sesConnection.openChannel("exec");
        ((ChannelExec)channel).setCommand(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream(), "UTF-8"));
        channel.connect();
        channel.disconnect();

        return outputBuffer.toString();
    }

    public void close()
    {
        sesConnection.disconnect();
    }
}
