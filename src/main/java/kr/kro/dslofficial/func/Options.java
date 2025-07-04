package kr.kro.dslofficial.func;

import kr.kro.dslofficial.ColorText;
import kr.kro.dslofficial.Main;
import kr.kro.dslofficial.Util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

public class Options extends Util {
    public static void run() {
        do {
            clearConsole();
            System.out.println();

            List<String> menu = Arrays.asList("업데이터 초기화", "mods폴더 위치 변경", "기본 ICT서버 비우기", "돌아가기");

            switch (printMenu(menu, "모드 업데이터 설정") + 1) {
                case 1 -> {
                    System.out.println();
                    if (!ask("정말 초기화 하시겠습니까? " + ColorText.text("이 작업은 되돌릴 수 없습니다!", "red", "none", true, false, false))) break;
                    Main.printMessage("info", "Request processing started.");

                    try {
                        FileWriter writer = new FileWriter(Main.fileList.get(0));
                        writer.write("");
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }

                    Main.printMessage("info", "초기화가 완료되었습니다.");
                    Util.pause(2000);
                    System.exit(0);
                }

                case 2 -> {
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
                    Main.printMessage("info", "현재 mods폴더 경로 : " + ColorText.text(Main.modsDir.toString(), "yellow", "none", true, false, false));
                    System.out.println();
                    do {
                        String input = input("바꿀 mods폴더의 경로를 입력해주세요. 원치 않는 경우 'exit'을 입력하여 뒤로 돌아갈 수 있습니다.");
                        if (input.equals("exit")) break;
                        else {
                            File f = new File(input);
                            if (!f.exists() | f.isFile()) {
                                Main.printMessage("error", "경로를 잘못 입력하셨습니다. 다시 입력해주세요.");
                                continue;
                            }

                            try {
                                JSONParser parser = new JSONParser();
                                FileReader reader = new FileReader(Main.fileList.get(0));
                                JSONObject obj = (JSONObject) parser.parse(reader);

                                // obj to map
                                Map<String, String> map = new HashMap<>();
                                for (Object o : obj.keySet()) {
                                    String key = (String) o;
                                    map.put(key, obj.get(key).toString());
                                }

                                map.put("path", input);

                                // map to obj and write it in.
                                JSONObject obj_new = new JSONObject(map);
                                FileWriter writer = new FileWriter(Main.fileList.get(0));
                                writer.write(obj_new.toJSONString());
                                writer.flush();
                                writer.close();

                                Main.modsDir = f;

                                Main.printMessage("info", "mods폴더 변경이 완료되었습니다.");
                                Util.pause(2000);
                                break;
                            } catch (IOException | ParseException e) {
                                e.printStackTrace();
                                System.exit(-1);
                            }
                        }
                    } while (true);
                }

                case 3 -> {
                    System.out.println();
                    if (ask("기본 ICT서버를 비워두시겠습니까? (자동검사 시 ICT서버를 직접 선택하셔야 합니다.)")) {
                        try {
                            org.json.JSONObject object = getContent("updater.dat", org.json.JSONObject.class);
                            File f = getContent("updater.dat", File.class);
                            FileWriter writer = new FileWriter(f);
                            object.put("default", new JSONObject());
                            writer.write(object.toString(4));
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                case 4 -> {
                    return;
                }

                default -> {
                    Main.printMessage("error", "잘못 입력하셨습니다. 다시 돌아갑니다...");
                    Util.pause(2000);
                }
            }
        } while (true);
    }
}
