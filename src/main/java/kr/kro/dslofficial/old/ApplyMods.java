package kr.kro.dslofficial.old;

import static kr.kro.dslofficial.Main.printMessage;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ApplyMods {
    public ApplyMods() {
        try {
            JSONParser parser = new JSONParser();

            File dataFile = new File(System.getProperty("user.dir") + File.separator + "updater.dat");
            FileReader reader = new FileReader(dataFile);

            JSONObject data = (JSONObject) parser.parse(reader);

            File localModDir = new File(data.get("path").toString());
            if (localModDir.listFiles() == null) throw new IOException();
            for (File f : Objects.requireNonNull(localModDir.listFiles())) if (!f.isDirectory()) Files.delete(f.toPath());

            File tempDir = new File(System.getProperty("user.dir") + File.separator + "temp");
            if (tempDir.listFiles() == null) throw new IOException();

            for (File f : Objects.requireNonNull(tempDir.listFiles())) {
                if (f.getName().split("\\.")[f.getName().split("\\.").length - 1].equalsIgnoreCase("jar")) {
                    Files.copy(f.toPath(), new File(localModDir + File.separator + f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            reader.close();
        } catch (IOException | ParseException e) {
            try {
                printMessage("error", "파일 I/O 오류입니다.");
                Thread.sleep(2000);
                System.exit(-1);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }
}
