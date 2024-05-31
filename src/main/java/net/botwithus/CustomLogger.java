package net.botwithus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomLogger {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final List<String> logMessages = new ArrayList<>();

    public static void log(String message) {
        LocalDateTime now = LocalDateTime.now();
        logMessages.add(dtf.format(now) + " - " + message);
    }

    public static List<String> getLogMessages() {
        return logMessages;
    }
}