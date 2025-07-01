package kr.kro.dslofficial;

import org.apache.commons.io.FileUtils;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Util {
    public static boolean ask(String message) {
        Scanner scan = new Scanner(System.in);
        System.out.print(ColorText.text(message, "yellow", "none", true, false, false) + " " + ColorText.text("[Yes/No] ", "white", "none", false, false, false) + " : ");
        String input = scan.nextLine();
        return input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("Yes");
    }

    public static String input(String message) {
        Scanner scan = new Scanner(System.in);
        System.out.println(ColorText.text(" " + message + " ".repeat(Main.width - (message.length() + 1)), "black", "white", true, false, false));
        return scan.nextLine();
    }

    public static File modsDir;
    public static List<File> fileList = new ArrayList<>();
    public static boolean initialize() {
        String currentDir = System.getProperty("user.dir") + File.separator;

        // instance
        File dataFile = new File(currentDir + "updater.dat");

        // Register files and directories
        fileList.add(new File(currentDir + "updater.dat"));
        fileList.add(new File(currentDir + "temp"));

        boolean first = false;
        try {
            // create temp folder
            if (fileList.get(1).exists()) FileUtils.cleanDirectory(fileList.get(1));
            else if (!fileList.get(1).mkdir()) throw new IOException();

            for (File f : fileList) {
                if (!f.exists() & f.getName().equalsIgnoreCase("updater.dat")) {
                    return true; // Updater.dat이 없는 경우
                } else if (f.getName().equalsIgnoreCase("updater.dat")) {
                    // Initialize
                    try {
                        JSONObject obj = new JSONObject(Files.readString(f.toPath()));
                        File modsFolder = new File(obj.get("path").toString());
                        if (!modsFolder.exists()) throw new ParseException(0);
                        else modsDir = modsFolder;
                    } catch (ParseException e) {
                        FileWriter writer = new FileWriter(dataFile);
                        writer.write("");
                        writer.flush();
                        writer.close();
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("업데이터 초기화에 실패했습니다.");
            e.printStackTrace();
            System.exit(-1);
        }
        return false;
    }

    public static <T> T getContent(String filename, Class<T> targetClass) {
        try {
            for (File f : fileList) {
                if (f.getName().equals(filename)) {
                    Object result = switch (targetClass.getSimpleName()) {
                        case "JSONObject" -> new JSONObject(Files.readString(f.toPath()));
                        case "JSONArray" -> new JSONArray(Files.readString(f.toPath()));
                        default -> throw new IllegalArgumentException("지원하지 않는 클래스입니다.");
                    };

                    if (!targetClass.isInstance(result)) {
                        throw new Error("getContent(String, Class<T>); 인자 Class<T>에서 명시한 클래스와 파싱된 파일의 클래스가 상이합니다.");
                    } else return targetClass.cast(result);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        throw new IllegalArgumentException("파일을 찾을 수 없습니다");
    }

    public static <T> T parseStr(String content, Class<T> targetClass) {
        Object result = switch (targetClass.getSimpleName()) {
            case "JSONObject" -> new JSONObject(content);
            case "JSONArray" -> new JSONArray(content);
            default -> throw new IllegalArgumentException("지원하지 않는 클래스입니다.");
        };

        if (!targetClass.isInstance(result)) {
            throw new Error("getContent(String, Class<T>); 인자 Class<T>에서 명시한 클래스와 파싱된 파일의 클래스가 상이합니다.");
        } else return targetClass.cast(result);
    }

    public static void printTitle(String title) {
        // ===============[ TITLE ]===============
        //                ^^     ^^
        System.out.println(ColorText.text("=", "yellow", "none", false, false, false).repeat((Main.width - (title.length() + 4)) / 2) + " [ " + title + " ] " + ColorText.text("=", "yellow", "none", false, false, false).repeat((Main.width - (title.length() + 4)) / 2));
    }

    public static void printMenu(List<String> items) {
        int i = 1;
        for (String item : items) {
            System.out.println(ColorText.text("[" + i + "] ", "green", "none", true, false, false) + item);
            i++;
        }
    }

    public static void clearConsole() {
        for (int i = 0; i < 100; i++) {
            System.out.println();
        }
    }

    public static void pause(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void unzip(Path zipFilePath, Path destDir) {
        try {
            if (!Files.exists(destDir)) {
                Files.createDirectories(destDir);
            }

            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path newPath = destDir.resolve(entry.getName()).normalize();

                    // Zip slip 공격 방지
                    if (!newPath.startsWith(destDir)) {
                        throw new IOException("압축 파일에 유효하지 않은 경로가 포함되어 있습니다: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        Files.createDirectories(newPath);
                    } else {
                        Files.createDirectories(newPath.getParent());
                        Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    zis.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String encrypt(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());

            byte[] bytes = md.digest();
            StringBuilder builder = new StringBuilder();

            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return null;
    }

    public static void printMessage(String type, String message) {
        switch (type) {
            case "info" -> System.out.println("[" + ColorText.text("Updater", "blue", "none", false, false, false) + "/" + ColorText.text("INFO", "green", "none", true, false, false) + "] : " + message);
            case "error" -> System.out.println("[" + ColorText.text("Updater", "blue", "none", false, false, false) + "/" + ColorText.text("ERROR", "red", "none", true, false, false) + "] : " + message);
            case "warn" -> System.out.println("[" + ColorText.text("Updater", "blue", "none", false, false, false) + "/" + ColorText.text("WARN", "yellow", "none", true, false, false) + "] : " + message);
        }
    }

    public static volatile String message;
    public static volatile String input = null;
    public static volatile boolean typed = false;
    public static String waitInput(String message_arg, int waitMilesec) {
        message = (message_arg == null) ? "" : message_arg;

        Scanner scan = new Scanner(System.in);
        Thread inputThread = new Thread(() -> {
            System.out.print(message);
            input = scan.nextLine();
            typed = true;
        });

        inputThread.setDaemon(true);
        inputThread.start();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < waitMilesec) {
            if (typed) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        if (input != null) return input;
        return null;
    }
}