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
import java.util.Collections;

public class Logs {

    private static Path allPath;
    private static Path librarianPath;
    private static Path studentsPath;
    private static Path[] securitiesPath;
    private static Path[] tablesPath;

    public static void init(int floorsNumber) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = dateFormat.format(timestamp);

        String logsFolder = "logs/" + time + "/";
        String securitiesFolder = "logs/" + time + "/securities/";
        String tablesFolder = "logs/" + time + "/tables/";
        String allLogsFile = logsFolder + "all_logs.log";
        String librarianLogsFile = logsFolder + "librarian_logs.log";
        String studentsLogsFile = logsFolder + "students_logs.log";
        String[] securitiesLogsFiles = new String[floorsNumber];
        String[] tablesLogsFiles = new String[floorsNumber];
        for(int i=0; i<floorsNumber; i++){
            securitiesLogsFiles[i] = securitiesFolder + "security" + i + "_logs.log";
            tablesLogsFiles[i] = tablesFolder + "tables" + i + "_logs.log";
        }

        File dir = new File("logs/");
        if(!dir.exists()){
            dir.mkdir();
        }

        dir = new File(logsFolder);
        if(!dir.exists()){
            dir.mkdir();
        }

        dir = new File(securitiesFolder);
        if(!dir.exists()){
            dir.mkdir();
        }

        dir = new File(tablesFolder);
        if(!dir.exists()){
            dir.mkdir();
        }

        allPath = Paths.get(allLogsFile);
        librarianPath = Paths.get(librarianLogsFile);
        studentsPath = Paths.get(studentsLogsFile);
        securitiesPath = new Path[floorsNumber];
        tablesPath = new Path[floorsNumber];
        for(int i=0; i<floorsNumber; i++){
            securitiesPath[i] = Paths.get(securitiesLogsFiles[i]);
            tablesPath[i] = Paths.get(tablesLogsFiles[i]);
        }
    }

    public static void write(String content, String file, int i) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss]");
        Path path;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = dateFormat.format(timestamp);
        content = time + " :: " + content;

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
                path = tablesPath[i];
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
