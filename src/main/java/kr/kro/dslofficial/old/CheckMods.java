package kr.kro.dslofficial.old;

import static kr.kro.dslofficial.Main.printMessage;

//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class CheckMods {
    public boolean checkMods() {
        try {
            printMessage("info", "tmp를 초기화합니다...");
            File tempDir = new File(System.getProperty("user.dir") + File.separator + "temp");
            if (tempDir.listFiles() != null) for (File f : Objects.requireNonNull(tempDir.listFiles())) if(!f.isDirectory()) Files.delete(f.toPath());

            printMessage("info", "모드URL서버에 접속");
            URL url = new URL("http://mods.dslofficial.kro.kr");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) throw new IOException();

            StringBuilder resultBuilder = new StringBuilder();
            BufferedReader bfr = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            do resultBuilder.append(bfr.readLine()); while (bfr.readLine() != null);

            // zip URL
            url = new URL(resultBuilder.toString());
            conn = (HttpURLConnection)  url.openConnection();
            File zipFile = new File(System.getProperty("user.dir") + File.separator + "temp" + File.separator + "mods.zip");
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(zipFile);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                printMessage("info", "파일을 읽어옵니다... (" + zipFile.length() + ")");
                fos.write(buffer, 0 , bytesRead);
            }


            printMessage("info", "파일 받기 완료.");

            // unzip
            printMessage("info", "서버 모드파일을 읽는중입니다. 잠시만 기다려 주십시오...");
            ZipFile zipTarget = new ZipFile(zipFile.getPath());
            zipTarget.extractAll(System.getProperty("user.dir") + File.separator + "temp");

            // get local modlist
//            JSONParser parser = new JSONParser();
            File updaterDataFile = new File(System.getProperty("user.dir") + File.separator + "updater.dat");
            FileReader reader = new FileReader(updaterDataFile);

//            JSONObject updaterData = (JSONObject) parser.parse(reader);
//            File localModDirPath = new File(updaterData.get("path").toString());

//            if (localModDirPath.listFiles() == null) {
//                printMessage("info", "적합성 검사가 완료되었습니다.");
//                Thread.sleep(2000);
//                return false;
//            }

            HashMap<String, Long> localModList = new HashMap<>();
//            for (File f : Objects.requireNonNull(localModDirPath.listFiles())) {
//                if (f.getName().split("\\.")[f.getName().split("\\.").length - 1].equalsIgnoreCase("jar")) localModList.put(f.getName(), f.length());
//            }

            fos.close();
            is.close();
            reader.close();

            // get server modlist
            if (tempDir.listFiles() == null) {
                reader.close();
                bfr.close();
                is.close();
                throw new IOException();
            }
            HashMap<String, Long> serverModList = new HashMap<>();

            for (File f : Objects.requireNonNull(tempDir.listFiles())) {
                if (f.getName().split("\\.")[f.getName().split("\\.").length - 1].equalsIgnoreCase("jar")) serverModList.put(f.getName(), f.length());
            }

            if (serverModList.isEmpty()) {
                reader.close();
                bfr.close();
                is.close();
                throw new IOException();
            }

            // check
            for (String key : serverModList.keySet()) {
                if (localModList.get(key) == null) {
                    printMessage("info", "적합성 검사가 완료되었습니다.");
                    Thread.sleep(2000);
                    return false;
                }
            }

            for (String key : localModList.keySet()) {
                if (serverModList.get(key) == null) {
                    printMessage("info", "적합성 검사가 완료되었습니다.");
                    Thread.sleep(2000);
                    return false;
                }
            }

            printMessage("info", "적합성 검사가 완료되었습니다.");
            Thread.sleep(2000);
            return true;
        } catch (MalformedURLException | InterruptedException | ZipException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            try {
                printMessage("error", "모드파일을 읽어오는 데에 오류가 발생했습니다.");
                Thread.sleep(2000);
                return false;
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
//        } catch (ParseException e) {
//            try {
//                printMessage("error", "updater.dat이 손상되었습니다.");
//                Thread.sleep(2000);
//                System.exit(-1);
//            } catch (InterruptedException e2) {
//                e2.printStackTrace();
//            }
//        }

        System.exit(-1);
        return false;
    }
}
