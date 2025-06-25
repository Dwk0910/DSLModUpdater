package kr.kro.dslofficial;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}