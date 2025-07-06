package kr.kro.dslofficial;

import kr.kro.dslofficial.pages.Autorun;

import java.io.File;
import java.io.IOException;

import java.lang.instrument.Instrumentation;

import java.util.Scanner;

public class Agent {
    public static volatile String input = null;
    public static volatile boolean typed = false;
    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        if (agentArgs.isEmpty()) System.exit(-1);

        String currentDir = System.getProperty("user.dir");
        String jarLoc = currentDir + File.separator + "DSLModUpdater" + File.separator + "DSLModUpdater.jar";
        String spairLoc = currentDir + File.separator + "DSLModUpdater.jar";

        // 새로운 CMD에서 실행 & Initialize
        if (agentArgs.split(";").length != 2) {
            if (new File(jarLoc).exists()) Runtime.getRuntime().exec("cmd /c start cmd.exe /k \"java -javaagent:" + jarLoc + "=" + agentArgs + ";notthefirst & exit");
            else if (new File(spairLoc).exists()) Runtime.getRuntime().exec("cmd /c start cmd.exe /k \"java -javaagent:" + spairLoc + "=" + agentArgs + ";notthefirst & exit");
            else System.out.println("DSLModUpdater.jar를 찾을 수 없습니다.");
            return;
        }

        if (Util.initialize()) {
            // 초기화 안됨
            Scanner scan = new Scanner(System.in);

            Thread inputThread = new Thread(() -> {
                System.out.print("업데이터 정보가 없습니다. 초기 설정을 하시려면 3초 이내에 ENTER키를 누르십시오...");
                input = scan.nextLine();
                typed = true;
            });

            inputThread.setDaemon(true);
            inputThread.start();

            // 3초 기다리고, 중간에 타입 받으면 종료
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 3000) {
                if (typed) break;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }

            try {
                if (input != null) {
                    if (new File(jarLoc).exists()) Runtime.getRuntime().exec("cmd /c start cmd.exe /k \"java -jar " + jarLoc + " & exit");
                    else if (new File(spairLoc).exists()) Runtime.getRuntime().exec("cmd /c start cmd.exe /k \"java -jar " + spairLoc + "& exit");
                    else {
                        try {
                            Util.printMessage("error", "DSLModUpdater.jar를 찾을 수 없습니다.");
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return;
        }

        // Agent 인자 확인 -> autorun 실행
        if (agentArgs.equals("--AsAgent")) {
            new Autorun();
        }
    }
}