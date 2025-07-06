package kr.kro.dslofficial.obj;

import kr.kro.dslofficial.ColorText;
import kr.kro.dslofficial.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loading implements Runnable {
    private static boolean running = true;
    private static String message;
    public Loading(String message) {
        Loading.message = message;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        List<String> frames_mac = new ArrayList<>(Arrays.asList("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"));
        List<String> frames_win = new ArrayList<>(Arrays.asList("-", "\\", "|", "/", "-", "\\", "|", "/"));

        while (running) {
            for (String frame : (System.getProperty("os.name").toLowerCase().contains("win") ? frames_win : frames_mac)) {
                System.out.print("\r" + frame + " " + ColorText.text(message, "white", "none", true, false, false));
                Util.pause(System.getProperty("os.name").toLowerCase().contains("win") ? 180 : 50);
            }
        }

        System.out.print(ColorText.text(" 완료\n", "green", "none", true, false, false));
        Util.pause(2000);
    }
}
