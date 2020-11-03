import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class systemctlStatusParser {
    private final  String[] text2Parse;
    private final String serviceName;

    public systemctlStatusParser(@NotNull String text, String service) throws Exception {
        text2Parse = text.split("\n");
        serviceName = service;

        verifyServiceName();

    }

    private void verifyServiceName() throws Exception {
        if (!text2Parse[0].contains(format("%s.service", serviceName)))
            throw new Exception(format("Expected %s.service but got %s", serviceName, text2Parse[0]));
    }

    public String getActiveStatus() throws Exception {
        for (String textLine: text2Parse) {
            if (textLine.replaceAll("\\s+","").startsWith("Active"))
                return textLine.split(": ")[1].split(" since")[0];;
        }

        throw new Exception(format("Expected 'Active' line in output %s but wasn't found", String.join("\n", text2Parse)));
    }

    public Integer getMainPID() throws Exception {
        for (String line: text2Parse) {
            if (line.startsWith(" Main PID")) return Integer.parseInt(line.split(": ")[1].split(" \\(")[0]);
        }
        throw new Exception(format("Expected %s output line starting with 'MAIN PID' in %s", serviceName, String.join("\n", text2Parse)));
    }
}
