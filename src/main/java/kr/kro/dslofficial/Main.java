package kr.kro.dslofficial;

import kr.kro.dslofficial.func.ApplyMods;
import kr.kro.dslofficial.func.Options;
import kr.kro.dslofficial.func.Autorun;

import kr.kro.dslofficial.func.SetICT;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

import java.util.*;

public class Main extends Util {
    public static final String version = "v1.1.0";
    public static final int width = 80;
    public static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) throws URISyntaxException {
        clearConsole();
        printMessage("info", "DSL모드 업데이터입니다. 환영합니다. [ " + version + " ]");
        printMessage("info", "버전을 확인중입니다. 잠시만 기다려 주십시오...");
        try {
            // Version Validation
            URL url = new URI("http://upt.dslofficial.kro.kr/uptver.html").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                printMessage("error", "업데이트 서버와의 연결 실패입니다. 관리자에게 문의해주세요.");
                return;
            }

            byte[] bytes = connection.getInputStream().readAllBytes();
            String new_ver = new String(bytes).replaceAll("\\s+", "");
            if (!new_ver.equals(version)) {
                printMessage("error", "새로운 버전(" + new_ver + ")이 릴리즈되었습니다. 업데이트 후 실행해주세요.");
                // javaagent에 의한 실행일 경우
                if (args.length != 0 && args[0].equals("--ByAgent")) {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        } catch (IOException e) {
            printMessage("error", "예외 발생 : " + e.getMessage());
            return;
        }

        // 개발자 모드
        if (args.length != 0 && args[0].equals("--DEV")) {
            String pwd = scan.nextLine();
            if (!pwd.equals("dongwan0910")) return;

            do {
                List<String> menu = new ArrayList<>();
                menu.add("File -> Hash 변환");
                menu.add("ModList확인");

                printMenu(menu);
                String input = input("메뉴 선택 (q: exit)");

                if (input.equals("q")) break;

                switch (input) {
                    case "1" -> {
                        do {
                            String path = input("파일 경로 입력");
                            File f = new File(path);
                            if (!f.exists()) {
                                printMessage("error", "파일 경로 알 수 없음");
                                continue;
                            }

                            System.out.println(hashFile(f));
                            break;
                        } while (true);
                    }

                    case "2" -> {
                        // TODO: 파일리스트 출력 시스템
                    }

                    default -> {
                        printMessage("error", "알 수 없는 명령");
                        continue;
                    }
                }
            } while (true);
            System.exit(0);
        }

        boolean first = initialize();
        // args.length가 0이 아니고 (1 이상) args[0]이 --ByAgent이며, 첫실행(미초기화상태)이 아닐 경우
        boolean isAgentMode = args.length != 0 && args[0].equals("--ByAgent");
        if (isAgentMode && !first) {
            new Autorun();
            return;
        } else if (isAgentMode) {
            try {
                printMessage("error", "자동 업데이트는 초기설정 이후 사용 가능합니다.");
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        /* File Validation
        서버모드버전 (기록) : YYYYMMMDD.버전 ex) 2025FEB04.01

        updater.dat -> mods
        -> path, last (마지막 다운로드된 버전)

        temp/ -> temp.dat (SHA-256 encrypt)

        1. 초기화면
        2. 메뉴 (모드 적용하기(후술), 설정(후술), 업데이터 종료)
        - MENU : 모드 적용하기 -
         1. PRE적합성검사 결과를 맨 위에 띄우기. (temp에 기록 넣기) (최신버전(2025FEB04.01)이 릴리즈되었습니다. 현재 상태로는 서버에 접속이 불가능할 수도 있습니다.) 현재 버전 : 2025JAN01.01)
         2. 모드 적용하기 (경고창 추가)
        - MENU : 설정 -
         1. 업데이터 초기화 (updater.dat 초기화)
         2. mods폴더 위치변경
         */

        printMessage("info", "Initializing...");

        String currentDir = System.getProperty("user.dir") + File.separator;
        File dataFile = new File(currentDir + "updater.dat");

        // Main Screen
        System.out.println();
        System.out.println(ColorText.text(" DSL MOD UPDATER ", "blue", "none", true, false, false));
        System.out.println(ColorText.text("   - " + version + " - ", "gray", "none", false, true, false));
        System.out.println("\n안녕하세요 DSL모드 업데이트 도우미입니다.");
        System.out.println("DCS에서 모드 업데이트 현황에 새로운 사항이 업로드되면 이곳에서 모드들을 한 번에 다운받으실 수 있습니다.");
        System.out.println("또한, 모드 요청도 DCS에 하실 수 있으니 많은 관심 부탁드립니다.");
        System.out.println("\n모드를 설치하시는 것에 조금이라도 번거로움을 덜고자 열심히 개발하였으니 많이 사용해주세요!");
        System.out.println("감사합니다.");

        System.out.println(ColorText.text("\n* 이 프로그램을 사용하시며 사용자분은 아래 내용에 동의하시게 됩니다. *", "blue", "none", true, false, false));
        System.out.println(ColorText.text("- 이 프로그램을 DSL과 DCS에 가입되어 있지 않은 제 3자에게 배포하지 않는다.", "yellow", "none", false, false, false));
        System.out.println(ColorText.text("- 이 프로그램을 사전 허가 없이 수정하지 않는다.", "yellow", "none", false, false, false));
        System.out.println(ColorText.text("Copyright 2024-2025. DSL All rights reserved.", "red", "none", true, false, false));
        System.out.println("\n" + ColorText.text("[ 프로그램을 사용하시며 생긴 버그의 제보는 개발자 @dongwan0910, @neatore DM으로 부탁드립니다. 감사합니다! ]", "green", "none", true, false, false));
        input("ENTER키를 눌러 프로그램을 시작합니다. Ctrl + C를 누르시면 프로그램을 종료시키실 수 있습니다.");

        if (first) {
            clearConsole();
            System.out.println(ColorText.text("==> 업데이터 초기설정", "green", "none", true, false, false));
            System.out.println("DSL서버 모드를 처음 적용하시는군요! 환영합니다.");
            System.out.println("모드가 설치될 곳인 'mods'폴더의 위치가 필요합니다.");
            System.out.println("아래 입력란에 'mods'폴더의 " + ColorText.text("절대경로", "white", "none", true, false, false) + "를 입력해주세요! (한 번 입력하시면 자동으로 저장됩니다)");
            System.out.println();
            System.out.println(ColorText.text("==================== [ 절대경로 찾는법 ] ====================", "yellow", "none", true, false, false));
            System.out.println("1. Win + R 단축키를 누른다.");
            System.out.println("2. '실행' 창에 '%appdata%'를 입력한다.");
            System.out.println("3. 파일 탐색기 창이 뜰 텐데, 그곳에서 .minecraft 폴더를 찾아 들어간다.");
            System.out.println("4. 'mods'폴더를 찾아 들어간다. 없으면 새로 만든다. (Forge가 설치되어 있어야 함!!)");
            System.out.println("4-1. 확인사항 : 폴더 안에 아무것도 없어야 한다. " + ColorText.text("설치 시 'mods'폴더 안의 내용이 모두 날아가니 주의!!", "red", "none", true, false, false));
            System.out.println("5. 탐색기 위쪽에 있는 주소창(~~~ > ~~~ > Roaming > .minecraft > mods 이런식으로 적혀있는 칸)의 빈 오른쪽 부분을 마우스로 클릭한다.");
            System.out.println("6. 주소창이 전체 선택되어 글 형식으로 바뀌면 그것을 복사하여 입력란에 넣는다.");
            System.out.println();

            do {
                File f = new File(
                        System.getProperty("user.home") +
                        File.separator +
                        "AppData" +
                        File.separator +
                        "Roaming" +
                        File.separator +
                        ".minecraft" +
                        File.separator +
                        "mods"
                );
                if (!ask("기본 경로 (" + f.toPath() + ") 를 찾았습니다. 자동으로 등록하시겠습니까?") || !f.exists()) {
                    if (!f.exists()) printMessage("warn", "기본 경로 (" + ColorText.text(f.toPath().toString(), "blue", "none", false, false, false) + ") 를 찾을 수 없습니다. 수동으로 입력을 받아야 합니다.");
                    String typedModsFolderPath = input("마인크래프트 'mods'폴더의 절대경로를 적어주세요");
                    f = new File(typedModsFolderPath);
                }

                if (!f.exists() || f.isFile()) {
                    printMessage("error", "\n경로를 잘못 입력하셨습니다. 다시 시도해주세요.\n");
                    continue;
                } else {
                    try {
                        Map<String, Object> mapObj = new HashMap<>();
                        mapObj.put("path", f.toPath());
                        mapObj.put("ICT", new JSONArray());
                        mapObj.put("default", new JSONObject());

                        JSONObject obj = new JSONObject(mapObj);

                        FileWriter writer = new FileWriter(dataFile);
                        writer.write(obj.toString(4));
                        writer.flush();

                        modsDir = f;

                        printMessage("info", "mods폴더가 성공적으로 등록되었습니다.");
                        pause(2000);
                        clearConsole();
                        break;
                    } catch (IOException e) {
                        printMessage("error", "예외가 발생했습니다 : " + e.getMessage());
                        return;
                    }
                }
            } while (true);
        }

        do {
            clearConsole();
            printTitle("DSL 모드 업데이트 관리자");
            System.out.println();
            System.out.println("Version " + ColorText.text(version, "blue", "none", true, false, false));
            System.out.println(ColorText.text("Copyright 2024-2025. DSL All rights reserved.\n", "gray", "none", false, false, false));
            System.out.println();

            System.out.println(ColorText.text("· MENU", "blue", "none", true, false, false));
            System.out.println();

            List<String> menu = new ArrayList<>();
            menu.add(ColorText.text("ICT서버 설정", "white", "none", true, false, false));
            menu.add("수동 모드 적용하기");
            menu.add("설정");
            menu.add("업데이터 종료");

            Util.printMenu(menu);

            System.out.println();
            String typed_str = input("선택하실 메뉴 번호를 입력해 주세요");

            switch (typed_str) {
                case "1" -> {
                    SetICT.run();
                    continue;
                }

                case "2" -> {
                    ApplyMods.run();
                    continue;
                }

                case "3" -> {
                    Options.run();
                    continue;
                }

                case "4" -> {
                    if (ask("업데이터를 종료하시겠습니까?")) {
                        printMessage("info", "모드 업데이터를 종료합니다.");
                        return;
                    } else continue;
                }

                default -> {
                    printMessage("error", "잘못 입력하셨습니다. 메인 화면으로 돌아갑니다...");
                    Util.pause(2000);
                    continue;
                }
            }
        } while (true);
    }

    public static void clearConsole() {
        for (int i = 1; i <= 100; i++) System.out.println();
    }
}