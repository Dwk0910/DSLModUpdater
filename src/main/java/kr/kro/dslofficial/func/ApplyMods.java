package kr.kro.dslofficial.func;

import kr.kro.dslofficial.ColorText;
import kr.kro.dslofficial.Loading;
import kr.kro.dslofficial.Main;
import kr.kro.dslofficial.Util;

import org.apache.commons.io.FileUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.*;

public class ApplyMods extends Util {
    private static final File temp_dir = Main.fileList.get(1);
    private static final File tempdata = new File(temp_dir + File.separator + "mods.dat");
    private static final JSONParser parser = new JSONParser();

    private static URL modzipurl;

    private static String official_latest;
    private static String local_latest;
    public static void run() {
        do {
            clearConsole();
            printTitle("모드 다운로드");

            try {
                Main.printMessage("info", "모드 URL서버에 접속중입니다...");
                URL url = new URI("http://upt.dslofficial.kro.kr/mods.html").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Main.printMessage("error", "URL서버와 연결에 실패했습니다. 관리자에게 문의해주세요.");
                    Util.pause(1000);
                    return;
                }

                byte[] bytes = conn.getInputStream().readAllBytes();
                modzipurl = new URI(new String(bytes).replaceAll("\\s+", "")).toURL();
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            boolean first = false;
            try {
                File f = Main.fileList.get(0);
                FileReader reader = new FileReader(f);
                JSONObject obj = (JSONObject) parser.parse(reader);
                if (obj.get("last") == null) first = true;
                else local_latest = (String) obj.get("last");
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            if (first) {
                if (ask("모드를 한 번도 적용하지 않으신 것으로 보입니다. 모드 호환성을 확인하려면 모드를 적용해야 합니다. 지금 모드를 다운받고 적용받으시겠습니까?\n" + ColorText.text("(대상 폴더 : " + Main.modsDir + ")", "blue", "none", false, false, false) + ColorText.text("\n* 모드 적용시 기존 폴더에 있던 모든 내용이 삭제됩니다!! *", "red", "none", true, false, false))) {
                    downloadMods();
                    applyMods();
                    continue;
                } else return;
            }

            // Main Screen
            try {
                // Get latest mods version name
                URL url = new URI("http://upt.dslofficial.kro.kr/modsver.html").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Main.printMessage("error", "모드 업데이트 버전확인 서버와 연결에 실패했습니다. 관리자에게 문의해주세요.");
                    Util.pause(1000);
                    return;
                }

                official_latest = new String(conn.getInputStream().readAllBytes()).replaceAll("\\s+", "");
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            System.out.println();
            System.out.println("서버 최신 모드 버전 : " + official_latest);
            System.out.print("마지막으로 다운받은 버전 : ");
            if (official_latest.equals(local_latest)) {
                System.out.println(ColorText.text(local_latest, "green", "none", true, false, false));
                System.out.println();
                System.out.println(ColorText.text("마지막으로 다운받은 버전이 서버 최신 버전과 동일합니다.", "white", "none", true, false, false));
                System.out.println(ColorText.text("서버에 접속되지 않는다면 서버모드 적용을 해보시기 바랍니다.", "yellow", "none", false, false, false));
                System.out.println();
            }
            else {
                System.out.println(ColorText.text(local_latest, "red", "none", true, false, false));
                System.out.println();
                Main.printMessage("warn", "최신 버전 " + ColorText.text("(" + official_latest + ")", "white", "none", true, false, false) + "이 릴리즈 되었습니다. 현재 버전 상태로는 접속이 불가능할 수 있습니다.");
                System.out.println();
            }

            // menu : 적합성확인, 적용
            List<String> items = new ArrayList<>();
            items.add("서버모드 적용");
            items.add("서버모드 적합성 검사");
            items.add("뒤로가기");

            Main.printMenu(items);
            System.out.println();
            String s = input("선택하실 메뉴 번호를 입력해 주세요");

            switch (s) {
                case "1" -> {
                    if (ask("대상 폴더 : " + Main.modsDir.getPath() + "\n" + ColorText.text("모드 적용시 기존 폴더에 있던 모든 내용이 삭제됩니다. 계속하시겠습니까?", "red", "none", true, false, false))) {
                        downloadMods();
                        applyMods();
                    }
                }

                case "2" -> {
                    boolean same = true;
                    downloadMods();
                    if (Main.modsDir.listFiles() != null & temp_dir.listFiles() != null) {
                        // 2년전의나 개천잰데? (HashMap 만들어서 크기(length)로 파일이 같은지 비교)

                        // define
                        Map<String, String> localMods = new HashMap<>();
                        for (File f : Objects.requireNonNull(Main.modsDir.listFiles())) {
                            if (f.isDirectory()) continue;
                            if (f.getName().split("\\.")[f.getName().split("\\.").length - 1].equalsIgnoreCase("jar"))
                                localMods.put(f.getName(), Long.toString(f.length()));
                        }

                        Map<String, String> tempMods = new HashMap<>();
                        for (File f : Objects.requireNonNull(temp_dir.listFiles())) {
                            if (f.isDirectory()) continue;
                            if (f.getName().split("\\.")[f.getName().split("\\.").length - 1].equalsIgnoreCase("jar"))
                                tempMods.put(f.getName(), Long.toString(f.length()));
                        }

                        // Case 01 : local에 있는 mods가 temp_dir안의 mods보다 수가 많을 경우
                        for (String key : localMods.keySet()) {
                            if (tempMods.get(key) == null) { same = false; break; }
                                // compare length
                            else if (!tempMods.get(key).equals(localMods.get(key))) { same = false; break; }
                        }

                        // Case 02 : temp_dir안의 mods가 local mods보다 수가 많을 경우
                        for (String key : tempMods.keySet()) {
                            if (localMods.get(key) == null) { same = false; break; }
                            else if (!localMods.get(key).equals(tempMods.get(key))) { same = false; break; }
                        }
                    }

                    Loading instance = new Loading("마무리 중...");
                    Thread thread = new Thread(instance);
                    thread.start();

                    try {
                        FileUtils.cleanDirectory(temp_dir);
                        instance.stop();
                        thread.join();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }

                    System.out.println();
                    String result = (same) ? ColorText.text("서버와 호환됩니다.", "green", "none", true, false, false) : ColorText.text("서버와 호환되지 않습니다. '서버모드 적용'을 시도해 보세요.", "red", "none", true, false, false);
                    System.out.print(ColorText.text("적합성 검사 결과 : ", "white", "none", true, false, false));
                    System.out.println(result);
                    System.out.println();

                    input("ENTER을 눌러 돌아갑니다.");
                }

                case "3" -> { return; }

                default -> {
                    Main.printMessage("error", "잘못 입력하셨습니다. 다시 돌아갑니다...");
                    Util.pause(1000);
                    continue;
                }
            }
        } while (true);
    }

    public static void downloadMods() {
        System.out.println();
        Main.printMessage("info", "Request processing started.");
        try {
            FileUtils.cleanDirectory(temp_dir);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Loading loadingInstance = new Loading("모드를 가져오고 있습니다...");

        Thread loadingThread = new Thread(loadingInstance);
        loadingThread.start();

        Path savePath = Path.of(temp_dir + File.separator + "mods.zip");
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(modzipurl.toURI())
                    .build();
            HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(savePath));
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            loadingInstance.stop();
            loadingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        loadingInstance = new Loading("모드 압축 해제중입니다...");

        loadingThread = new Thread(loadingInstance);
        loadingThread.start();

        unzip(savePath, temp_dir.toPath());
        if (!savePath.toFile().delete()) throw new Error("IOException occurred.");

        try {
            loadingInstance.stop();
            loadingThread.join(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void applyMods() {
        File modsDat = new File(temp_dir + File.separator + "mods.dat");
        if (!modsDat.exists()) {
            Main.printMessage("error", "mods.dat을 찾을 수 없습니다. 관리자에게 문의해주세요.");
            System.exit(-1);
        }

        try {
            Loading instance_1 = new Loading("데이터 파일을 업데이트 하는 중입니다...");
            Thread loadingThread = new Thread(instance_1);
            loadingThread.start();

            // Adding latest version info to updater.dat
            FileReader reader = new FileReader(modsDat);
            JSONObject modsdat_obj = (JSONObject) parser.parse(reader);

            String latest_ver = modsdat_obj.get("version").toString();
            File f = Main.fileList.get(0);

            reader.close();
            reader = new FileReader(f);
            JSONObject updaterdat_obj = (JSONObject) parser.parse(reader);
            reader.close();

            Map<String, String> updaterdat_map = new HashMap<>();
            for (Object o : updaterdat_obj.keySet()) {
                String key = (String) o;
                updaterdat_map.put(key, updaterdat_obj.get(key).toString());
            }
            updaterdat_map.put("last", latest_ver);

            updaterdat_obj = new JSONObject(updaterdat_map);
            FileWriter writer = new FileWriter(f);
            writer.write(updaterdat_obj.toJSONString());
            writer.flush();
            writer.close();

            local_latest = updaterdat_obj.get("last").toString();

            try {
                instance_1.stop();
                loadingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            // Apply mods
            Loading instance_2 = new Loading("모드를 적용중입니다...");
            loadingThread = new Thread(instance_2);
            loadingThread.start();

            FileUtils.cleanDirectory(Main.modsDir);

            for (File f2 : Objects.requireNonNull(temp_dir.listFiles())) {
                if (f2.getName().split("\\.")[f2.getName().split("\\.").length - 1].equalsIgnoreCase("jar")) Files.copy(f2.toPath(), new File(Main.modsDir.getPath() + File.separator + f2.getName()).toPath());
            }

            try {
                instance_2.stop();
                loadingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            // clear temp
            Loading instance_3 = new Loading("마무리 작업중입니다. 잠시만 기다려 주세요...");
            loadingThread = new Thread(instance_3);
            loadingThread.start();

            FileUtils.cleanDirectory(temp_dir);

            try {
                instance_2.stop();
                loadingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}