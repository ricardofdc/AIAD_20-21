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
    private static Path[] securitiesPath;
    private static Path tablesPath;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss");

    public static void init(int securitiesNumber) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = dateFormat.format(timestamp);

        String logsFolder = "logs/" + time + "/";
        String securitiesFolder = "logs/" + time + "/securities/";
        String allLogsFile = logsFolder + "all_logs.txt";
        String librarianLogsFile = logsFolder + "librarian_logs.txt";
        String studentsLogsFile = logsFolder + "students_logs.txt";
        String[] securitiesLogsFiles = new String[securitiesNumber];
        for(int i=0; i<securitiesNumber; i++){
            securitiesLogsFiles[i] = securitiesFolder + "security" + i + "_logs.txt";
        }
        String tablesLogsFile = logsFolder + "tables_logs.txt";

        File dir = new File("logs/");
        if(!dir.exists()){
            dir.mkdir();
        }

        dir = new File("logs/" + time + "/");
        if(!dir.exists()){
            dir.mkdir();
        }

        dir = new File("logs/" + time + "/securities/");
        if(!dir.exists()){
            dir.mkdir();
        }

        allPath = Paths.get(allLogsFile);
        librarianPath = Paths.get(librarianLogsFile);
        studentsPath = Paths.get(studentsLogsFile);
        securitiesPath = new Path[securitiesNumber];
        for(int i=0; i<securitiesNumber; i++){
            securitiesPath[i] = Paths.get(securitiesLogsFiles[i]);
        }
        tablesPath = Paths.get(tablesLogsFile);

    }

    public static void write(String content, String file, int i) {
        /*
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

         */
        Path path;

        switch (file) {
            case "librarian":
                path = librarianPath;
                break;
            case "student":
                path = studentsPath;
                break;
            case "security":
                path = securitiesPath[i];
                break;
            case "table":
                path = tablesPath;
                break;
            default:
                path = allPath;
                return;
        }

        try {
            Files.write(path, Collections.singletonList(content), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void write(String content, String file) {
        write(content, file, 0);
    }
}
