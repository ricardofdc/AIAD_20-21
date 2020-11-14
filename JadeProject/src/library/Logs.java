package library;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;

public class Logs {

    private static Path allPath;
    private static Path librarianPath;
    private static Path studentsPath;
    private static Path securitiesPath;
    private static Path tablesPath;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy(HH:mm:ss)");

    public static void init() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = dateFormat.format(timestamp);

        String logsFolder = "logs/";
        String allLogsFile = logsFolder + "all_logs_" + time + ".txt";
        String librarianLogsFile = logsFolder + "librarian_logs_" + time + ".txt";
        String studentsLogsFile = logsFolder + "students_logs_" + time + ".txt";
        String securitiesLogsFile = logsFolder + "securities_logs_" + time + ".txt";
        String tablesLogsFile = logsFolder + "tables_logs_" + time + ".txt";

        File dir = new File("logs/");
        if(!dir.exists()){
            dir.mkdir();
        }

        allPath = Paths.get(allLogsFile);
        librarianPath = Paths.get(librarianLogsFile);
        studentsPath = Paths.get(studentsLogsFile);
        securitiesPath = Paths.get(securitiesLogsFile);
        tablesPath = Paths.get(tablesLogsFile);

    }

    public static void write(String content, String file) {
        Path path = allPath;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = dateFormat.format(timestamp);

        content = time + " :: " + content;

        try {
            Files.write(path, Collections.singletonList(content), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (file) {
            case "librarian" -> path = librarianPath;
            case "student" -> path = studentsPath;
            case "security" -> path = securitiesPath;
            case "table" -> path = tablesPath;
            default -> { return; }
        }

        try {
            Files.write(path, Collections.singletonList(content), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
