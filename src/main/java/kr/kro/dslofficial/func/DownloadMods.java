package kr.kro.dslofficial.func;

import static kr.kro.dslofficial.Util.*;

import kr.kro.dslofficial.ColorText;
import kr.kro.dslofficial.Main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadMods {
    public static Map<String, String> statusMap = new HashMap<>();
    public static void run(JSONArray mods, File targetFolder) {
        ExecutorService service = Executors.newFixedThreadPool(mods.length());

        try {
            int digit = 1;
            for (int i = 0; i < mods.length(); i++) {
                int idx = i + 1;
                digit = Integer.toString(idx).length() + 1;
                statusMap.put(Integer.toString(idx), ColorText.text("다운로드 대기중", "gray", "none", false, false, false));

                JSONObject mod = mods.getJSONObject(i);

                service.submit(() -> {
                    statusMap.put(Integer.toString(idx), ColorText.text("다운로드 중", "yellow", "none", false, false, false));
                    if (downloadFileFromURL(mod.getString("URL"), new File(targetFolder.toPath() + File.separator + "MOD_" + idx + ".jar"))) {
                        statusMap.put(Integer.toString(idx), ColorText.text("다운로드 성공", "b-blue", "none", true, false, false));
                    } else
                        statusMap.put(Integer.toString(idx), ColorText.text("다운로드 실패", "red", "none", true, false, false));
                });
            }

            service.shutdown();
            clearConsole();

            do {
                pause(50);
                Main.tw.println("\u001B[H"); // Move Cursor to first position
                printMessage("info", "모드 " + ColorText.text(mods.length() + "개", "green", "none", true, false, false) + "를 다운로드 받는 중입니다. 잠시만 기다려 주십시오...");
                for (String key : statusMap.keySet()) {
                    Main.tw.println(" " + ColorText.text("[ " + key + " ".repeat(digit - key.length()) + "]", "green", "none", true, false, false) + " 번 째 모드..... " + statusMap.get(key));
                }
                Main.tw.flush();
            } while (!service.isTerminated() && statusMap.containsValue(ColorText.text("다운로드 중", "yellow", "none", false, false, false))); // DownloadStatus 출력 + ExecutorService가 완전히 종료될 때까지 대기
        } catch (JSONException e) {
            printMessage("info", "ICT서버의 mods배열이 손상되었습니다. ICT서버 관리자에게 문의하십시오.");
            pause(2000);
            System.exit(-1);
        }
    }
}
