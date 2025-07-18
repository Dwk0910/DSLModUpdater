package kr.kro.dslofficial;

import kr.kro.dslofficial.obj.ServerResponse;
import kr.kro.dslofficial.obj.enums.ConnectionStatus;

import org.apache.commons.io.FileUtils;

import org.jline.utils.InfoCmp;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Util {
    public static boolean ask(String message) {
        String prompt = ColorText.text(message, "yellow", "none", true, false, false) + " " + ColorText.text("[" + ColorText.text("Y", "white", "none", true, false, true) + "es/" + ColorText.text("N", "white", "none", true, false, true) + "o] ", "white", "none", false, false, false) + " : ";
        String input = Main.reader.readLine(prompt);
        return input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("Yes");
    }

    public static String input(String message) {
        return Main.reader.readLine(ColorText.text(" " + message + " ".repeat(Main.width - (message.length() + 1)) + "\n", "black", "white", true, false, false));
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
                        if (!modsFolder.exists()) throw new Exception();
                        else modsDir = modsFolder;
                    } catch (Exception e) {
                        Writer writer = new OutputStreamWriter(new FileOutputStream(dataFile), StandardCharsets.UTF_8);
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
                        case "File" -> f;
                        case "JSONObject" -> new JSONObject(Files.readString(f.toPath(), StandardCharsets.UTF_8));
                        case "JSONArray" -> new JSONArray(Files.readString(f.toPath(), StandardCharsets.UTF_8));
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
        Main.out.println(ColorText.text("=", "yellow", "none", false, false, false).repeat((Main.width - (title.length() + 4)) / 2) + " [ " + title + " ] " + ColorText.text("=", "yellow", "none", false, false, false).repeat((Main.width - (title.length() + 4)) / 2));
    }

    public static int printMenu(List<String> items, String title, String... anotherOption) {
        int selectedIdx = 0;
        if (items.isEmpty()) return selectedIdx;

        try {
            do {
                // Draw Menu
                Main.out.println("\u001B[H");

                printTitle(title);
                Main.out.println();
                if (anotherOption.length >= 1) Main.out.println(anotherOption[0]);

                Main.out.println(ColorText.text(" · MENU", "blue", "none", true, false, false));
                Main.out.println();
                if (anotherOption.length >= 2) Main.out.println(anotherOption[1]);
                Main.out.println("-".repeat(Main.width));
                for (int i = 0; i < items.size(); i++) {
                    if (i == selectedIdx) Main.out.println(ColorText.text(" > " + items.get(i), "green", "none", true, false, false));
                    else Main.t.writer().println("   " + ColorText.text(items.get(i), "gray", "none", false, false, false));
                }
                Main.out.println("-".repeat(Main.width));
                Main.t.flush();

                int key = Main.t.reader().read();

                if (key == 65 && selectedIdx - 1 >= 0) selectedIdx--;
                else if (key == 66 && selectedIdx + 1 < items.size()) selectedIdx++;
                else if (key == 10 || key == 13) return selectedIdx;
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void printArrayMenu(List<String> items) {
        int i = 1;
        for (String item : items) {
            Main.out.println(ColorText.text(" [" + i + "] ", "green", "none", true, false, false) + " " + ColorText.text(item, "white", "none", false, false, false));
            i++;
        }
    }

    public static void clearConsole() {
        Main.t.puts(InfoCmp.Capability.clear_screen);
        Main.t.flush();
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

    public static String hashFile(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            fis.close();

            StringBuilder builder = new StringBuilder();
            for (byte b : md.digest()) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printMessage(String type, String message) {
        switch (type) {
            case "info" -> Main.out.println("[" + ColorText.text("Updater", "blue", "none", false, false, false) + "/" + ColorText.text("INFO", "green", "none", true, false, false) + "] : " + message);
            case "error" -> Main.out.println("[" + ColorText.text("Updater", "blue", "none", false, false, false) + "/" + ColorText.text("ERROR", "red", "none", true, false, false) + "] : " + message);
            case "warn" -> Main.out.println("[" + ColorText.text("Updater", "blue", "none", false, false, false) + "/" + ColorText.text("WARN", "yellow", "none", true, false, false) + "] : " + message);
        }
    }

    public static volatile String message;
    public static volatile String input = null;
    public static volatile boolean typed = false;
    public static String waitInput(String message_arg, int waitMilesec) {
        Main.out.print(message_arg);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            Future<String> future = executor.submit(() -> Main.reader.readLine());
            future.get(waitMilesec, TimeUnit.MILLISECONDS);
            Main.out.println();
            executor.shutdownNow();
            return "";
        } catch (TimeoutException e) {
            executor.shutdownNow();
            Main.out.println();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            executor.shutdownNow();
            return null;
        }
    }

    public static ServerResponse getResponse(String urlstr) {
        try {
            URL url = new URL(urlstr);
            url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(100000);
            connection.setReadTimeout(100000);

            // Connection failed
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return new ServerResponse(null, ConnectionStatus.CONNERR);
            return new ServerResponse(new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8), ConnectionStatus.OK);
        } catch (MalformedURLException e) {
            return new ServerResponse(null, ConnectionStatus.INVALID_URL);
        } catch (IOException e) {
            return new ServerResponse(null, ConnectionStatus.CONNERR);
        }
    }

    public static boolean downloadFileFromURL(String URL, File target) {
        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(100000);
            conn.setReadTimeout(100000);

            FileOutputStream fos = new FileOutputStream(target);
            InputStream is = conn.getInputStream();

            fos.write(is.readAllBytes());
            fos.flush();
            fos.close();
            is.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isValidICTObject(org.json.JSONObject obj) {
        return !obj.isNull("name") || !obj.isNull("URL");
    }
}