package kr.kro.dslofficial.pages;

import kr.kro.dslofficial.Main;
import kr.kro.dslofficial.ColorText;
import kr.kro.dslofficial.Util;
import kr.kro.dslofficial.obj.ServerResponse;
import kr.kro.dslofficial.obj.enums.ConnectionStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
<< 2.0.0 업데이트 >>
메뉴 :
    1. ICT서버 설정하기 (Autorun Agent 위함)
    2. 모드 수동 적용 (이 Autorun Agent가 끝나지 않았음에도 마인크래프트가 실행되는 것을 확인함. 마인크래프트 실행이 모드 확인보다 먼저라면 에러가 나므로, 에러 발생시 이 수동적용 메뉴를 통해 직접 적용하라고 안내하여야 함.)
    3. 설정
    4. 업데이터 종료

파일 비교 방법 <<V 2.0.0>>
Util.hashFile(java.lang.File) 함수 사용
각각 해시값은 파일마다 고유하게 존재 (이름, 경로는 영향X 오직 파일 내용만 영향이 감)
모드서버에 올라간 해시와 비교해서 다르면 다운로드 서버에서 다운받은 뒤 교체.
이로 모드 무결성검사 시간도 매우 단축시킬 수 있음.
 */
public class SetICT extends Util {
    public static void run() {
        String input = "";
        do {
            clearConsole();
            System.out.println();

            initialize();
            JSONObject data = getContent("updater.dat", JSONObject.class);
            JSONArray ictList = new JSONArray(data.get("ICT").toString());

            if (!ictList.isEmpty()) {
                printMessage("info", "ICT서버를 추가하시려면 입력란에 add를 입력하십시오.");

                List<String> menuItem = new ArrayList<>();
                for (Object o : ictList) {
                    JSONObject obj = parseStr(o.toString(), JSONObject.class);
                    if (!isValidICTObject(obj)) {
                        printMessage("error", "updater.dat 파일이 손상되었습니다.");
                        System.exit(0);
                    } else {
                        try {
                            if (!parseStr(data.get("default").toString(), JSONObject.class).isNull("URL") && parseStr(data.get("default").toString(), JSONObject.class).get("URL").equals(parseStr(o.toString(), JSONObject.class).get("URL")))
                                menuItem.add(ColorText.text(obj.get("name").toString(), "green", "none", false, false, false) + " - " + ColorText.text(obj.get("URL").toString(), "yellow", "none", false, false, false) + ColorText.text(" (자동검사 기본서버)", "b-yellow", "none", true, false, false));
                            else
                                menuItem.add(ColorText.text(obj.get("name").toString(), "green", "none", false, false, false) + " - " + ColorText.text(obj.get("URL").toString(), "yellow", "none", false, false, false));
                        } catch (JSONException e) {
                            printMessage("error", "updater.dat 파일이 손상되었습니다.");
                            System.exit(0);
                        }
                    }
                }

                printTitle("ICT서버 관리");
                System.out.println();
                printArrayMenu(menuItem);
                System.out.println();

                input = input("관리할 서버의 번호를 입력하십시오. (서버추가: add, 뒤로가기: exit)");
            } else {
                // 등록된 ICT서버가 없을 경우 강제로 add메뉴 진입
                printTitle("ICT서버 관리");
                printMessage("warn", "등록된 ICT서버가 없습니다. ICT서버를 추가해 주십시오.");
                if (input.equals("add")) input = "exit";
                else input = "add";
            }

            if (input.equalsIgnoreCase("exit")) break;
            // ictList가 empty 상태일 경우 무조건 자동선택됨
            if (input.equalsIgnoreCase("add")) {
                System.out.println();
                printTitle("ICT서버 추가");
                String url = input("추가할 ICT서버의 URL를 입력해 주세요. (뒤로가기: exit)");
                if (url.equalsIgnoreCase("exit")) continue;
                if (!ask(ColorText.text(url, "blue", "none", true, false, false) + ColorText.text(" : 입력하신 URL이 맞습니까?", "yellow", "none", false , false, false))) continue;
                printMessage("info", "URL등록을 시작합니다.");

                if (alreadyExists(ictList, new JSONObject().put("URL", url), "URL")) {
                    printMessage("info", "이 URL은 이미 존재합니다.");
                    pause(2000);
                    continue;
                }

                printMessage("info", "URL 연결 상태를 확인중입니다.");

                ServerResponse response = getResponse(url);
                if (response.status == ConnectionStatus.INVALID_URL) {
                    printMessage("error", "URL을 잘못 입력하셨습니다. ('https://' 등도 포함했는지 확인하십시오)");
                    pause(2000);
                    continue;
                } else if (response.status == ConnectionStatus.CONNERR) {
                    printMessage("error", "서버와의 연결에 실패했습니다.");
                    pause(2000);
                    continue;
                }

                // 연결성공
                printMessage("info", "서버 연결에 성공했습니다. 내용을 불러오는 중입니다...");

                JSONObject obj;
                JSONArray mods;
                try {
                    obj = new JSONObject(response.response);
                    if (obj.isNull("version") || obj.isNull("mods")) throw new JSONException("");
                    else mods = new JSONArray(obj.get("mods").toString());
                } catch (JSONException e) {
                    printMessage("error", "올바른 ICT서버가 아닙니다.");
                    pause(2000);
                    continue;
                }

                if (!ask(ColorText.text("최신 버전 : ", "yellow", "none", false, false ,false) + ColorText.text(obj.get("version").toString(), "green", "none", true, false, false) + ColorText.text(", 모드 갯수 : ", "b-yellow", "none", false, false, false) + ColorText.text(mods.length() + "개", "blue", "none", true, false, false) + ColorText.text(" - 이 정보가 맞습니까?", "yellow", "none", true, false, false))) {
                    printMessage("info", "ICT서버 추가가 취소되었습니다.");
                    pause(2000);
                    continue;
                }

                String typedName;
                do {
                    typedName = input("이 서버를 무슨 이름으로 저장하시겠습니까?");
                    if (typedName == null) continue;
                    // 이름 중복 금지
                    else if (alreadyExists(ictList, new JSONObject().put("name", typedName), "name")) {
                        printMessage("error", "이 이름은 이미 존재합니다.");
                        continue;
                    }
                    else break;
                } while (true);


                ictList.put(new JSONObject().put("name", typedName).put("URL", url));
                data.put("ICT", ictList);

                try {
                    File f = getContent("updater.dat", File.class);
                    FileWriter writer = new FileWriter(f);
                    writer.write(data.toString(4));
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                printMessage("info", typedName + " : ICT서버 추가가 완료되었습니다.");
                // 기본 ICT서버가 없을 경우 이것으로 등록하겠냐 물어보기
                if (data.getJSONObject("default").isNull("name") && ask("자동검사 기본 ICT서버가 없습니다. 이 ICT서버를 자동검사 기본서버로 등록하시겠습니까?\n" + ColorText.text("자동검사 기본서버로 등록하시면, 마인크래프트 실행 시 아무것도 하지 않아도 모드적용이 가능합니다.", "yellow", "none", true, false, false))) {
                    data.put("default", new JSONObject().put("name", typedName).put("URL", url));
                    try {
                        File f = getContent("updater.dat", File.class);
                        FileWriter writer = new FileWriter(f);
                        writer.write(data.toString(4));
                        writer.flush();
                        writer.close();
                        printMessage("info", "등록이 완료되었습니다.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                pause(2000);
                continue;
            } else {
                int selected;

                try {
                    selected = Integer.parseInt(input);
                    if (ictList.isNull(selected - 1)) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    printMessage("error", "잘못된 값을 입력하셨습니다.");
                    pause(2000);
                    continue;
                }

                do {
                    // 최신화
                    initialize();
                    data = getContent("updater.dat", JSONObject.class);
                    ictList = parseStr(data.get("ICT").toString(), JSONArray.class);

                    clearConsole();

                    JSONObject ict = parseStr(ictList.get(selected - 1).toString(), JSONObject.class);
                    boolean isDefault = !parseStr(data.get("default").toString(), JSONObject.class).isNull("URL") && parseStr(data.get("default").toString(), JSONObject.class).get("URL").equals(ict.get("URL"));

                    String fixed = "\n" +
                            " [ " + ColorText.text(ict.get("name").toString(), "b-yellow", "none", true, false, true) + " ] " +
                            "\n" +
                            ColorText.text(ict.get("URL").toString(), "blue", "none", false, false, false) +
                            "\n" +
                            ((isDefault) ? ColorText.text(" - 자동검사 기본서버 -", "b-yellow", "none", true, false, false) + "\n" : "");

                    List<String> menu = Arrays.asList("이름 변경", "URL 변경", "기본 ICT 서버로 만들기", "검사", "삭제", "뒤로가기");
                    int typed = printMenu(menu, "ICT서버 관리", fixed) + 1;

                    // do문 break가 필요한 동작 5(삭제), 6(돌아가기)
                    if (typed == 5) {
                        // 삭제
                        if (ask("정말 ICT서버 " + ColorText.text(ict.get("name").toString(), "blue", "none", true, false, false) + ColorText.text("를 삭제하시겠습니까? ", "b-yellow", "none", false, false, false) + ColorText.text("이 작업은 되돌릴 수 없습니다!", "red", "none", true, false, false))) {
                            try {
                                // ICT Array에서 삭제
                                JSONArray newIctList = new JSONArray();
                                for (Object o : ictList) {
                                    JSONObject obj = new JSONObject(o.toString());
                                    if (!obj.get("name").equals(ict.get("name"))) newIctList.put(o);
                                }
                                data.put("ICT", newIctList);

                                // default에서 삭제
                                JSONObject defObj = new JSONObject(data.get("default").toString());
                                if (!defObj.isNull("name") && defObj.get("name").equals(ict.get("name"))) data.put("default", new JSONObject());

                                // 파일 쓰기
                                File f = getContent("updater.dat", File.class);
                                FileWriter writer = new FileWriter(f);
                                writer.write(data.toString(4));
                                writer.flush();
                                writer.close();

                                printMessage("info", "제거가 완료되었습니다.");
                                pause(2000);
                            } catch (IOException e) {
                                e.printStackTrace();
                                printMessage("error", "파일을 쓰는 중 오류가 발생했습니다.");
                                pause(2000);
                                continue;
                            }
                            break;
                        } else continue;
                    }
                    if (typed == 6) break;

                    switch (typed) {
                        case 1 -> {
                            // 이름변경
                            String typedName;
                            do {
                                typedName = input("바꿀 이름을 입력하여 주십시오. (취소: exit)");
                                if (typedName == null) continue;
                                // 이름 중복 금지
                                else if (alreadyExists(ictList, new JSONObject().put("name", typedName), "name")) {
                                    printMessage("error", "이 이름은 이미 존재합니다.");
                                    continue;
                                }

                                else break;
                            } while (true);
                            if (typedName.equals("exit")) continue;


                            changeFromICT(data, ict.get("name").toString(), "name", typedName);
                            printMessage("info", "이름 변경이 완료되었습니다.");
                            // 파일을 수정했으므로 다시 불러와야함
                            initialize();
                            pause(2000);
                            continue;
                        }

                        case 2 -> {
                            // URL변경
                            String typedURL;
                            do {
                                typedURL = input("바꿀 URL를 입력하여 주십시오. (취소: exit)");
                                if (typedURL == null) continue;
                                else break;
                            } while (true);
                            if (typedURL.equals("exit")) continue;
                            if (!ask(ColorText.text(typedURL, "blue", "none", true, false, false) + ColorText.text(" : 입력하신 URL이 맞습니까?", "yellow", "none", false, false, false)))
                                continue;
                            printMessage("info", "URL등록을 시작합니다.");

                            if (alreadyExists(ictList, new JSONObject().put("URL", typedURL), "URL")) {
                                printMessage("info", "이 URL은 이미 존재합니다.");
                                pause(2000);
                                continue;
                            }

                            printMessage("info", "URL 연결 상태를 확인중입니다.");

                            ServerResponse response = getResponse(typedURL);
                            if (response.status == ConnectionStatus.INVALID_URL) {
                                printMessage("error", "URL을 잘못 입력하셨습니다. ('https://' 등도 포함했는지 확인하십시오)");
                                pause(2000);
                                continue;
                            } else if (response.status == ConnectionStatus.CONNERR) {
                                printMessage("error", "서버와의 연결에 실패했습니다.");
                                pause(2000);
                                continue;
                            }

                            // 연결성공
                            printMessage("info", "서버 연결에 성공했습니다. 내용을 불러오는 중입니다...");

                            JSONObject obj;
                            JSONArray mods;
                            try {
                                obj = new JSONObject(response.response);
                                if (obj.isNull("version") || obj.isNull("mods"))
                                    throw new JSONException("");
                                else mods = new JSONArray(obj.get("mods").toString());
                            } catch (JSONException e) {
                                printMessage("error", "올바른 ICT서버가 아닙니다.");
                                pause(2000);
                                continue;
                            }

                            if (!ask(ColorText.text("최신 버전 : ", "yellow", "none", false, false, false) + ColorText.text(obj.get("version").toString(), "green", "none", true, false, false) + ColorText.text(", 모드 갯수 : ", "b-yellow", "none", false, false, false) + ColorText.text(mods.length() + "개", "blue", "none", true, false, false) + ColorText.text(" - 이 정보가 맞습니까?", "yellow", "none", true, false, false))) {
                                printMessage("info", "ICT서버 추가가 취소되었습니다.");
                                pause(2000);
                                continue;
                            }

                            changeFromICT(data, ict.get("name").toString(), "URL", typedURL);
                            printMessage("info", "URL 변경이 완료되었습니다.");

                            // 파일을 수정했으므로 다시 불러와야함
                            initialize();
                            pause(2000);
                            continue;
                        }

                        case 3 -> {
                            // 기본 ICT로 만들기
                            if (!data.isNull("default") && !parseStr(data.get("default").toString(), JSONObject.class).isNull("URL") && parseStr(data.get("default").toString(), JSONObject.class).get("URL").equals(ict.get("URL"))) {
                                printMessage("error", "이 서버는 이미 자동검사 기본 서버로 등록되어 있습니다.");
                                pause(2000);
                                continue;
                            }

                            try {
                                if (ask("이 서버를 자동검사 기본 서버로 등록하시겠습니까?")) {
                                    data.put("default", new JSONObject().put("name", ict.get("name")).put("URL", ict.get("URL")));
                                    File f = getContent("updater.dat", File.class);
                                    FileWriter fw = new FileWriter(f);
                                    fw.write(data.toString(4));
                                    fw.flush();
                                    fw.close();

                                    printMessage("info", "기본 서버로 등록되었습니다.");
                                    pause(2000);
                                }
                            } catch (IOException e) {
                                printMessage("error", "updater.dat이 손상되었습니다.");
                                pause(2000);
                                continue;
                            }
                        }

                        case 4 -> {
                            // 검사
                            try {
                                printMessage("info", "이 ICT 서버의 연결을 검사합니다.");
                                ServerResponse response = getResponse(ict.get("URL").toString());
                                if (response.status != ConnectionStatus.OK) throw new Exception();
                                JSONObject obj = new JSONObject(response.response);
                                if (obj.isNull("version") || obj.isNull("mods")) throw new Exception();
                                JSONArray array = new JSONArray(obj.get("mods").toString());
                                for (Object o : array) {
                                    JSONObject mod = new JSONObject(o.toString());
                                    if (mod.isNull("URL") || mod.isNull("hash")) throw new Exception();
                                }
                                String version = obj.get("version").toString();
                                int size = array.length();

                                printMessage("info", "ICT 서버가 올바릅니다.");
                                printMessage("info", "최신 버전 : '" + ColorText.text(version, "green", "none", true, false, false) + "', 사용 가능한 모드 : " + ColorText.text(size + "개", "blue", "none", true, false, false));
                                Main.out.print("게속하려면 ENTER를 누르십시오...");
                                Main.reader.readLine();
                            } catch (Exception e) {
                                printMessage("error", "서버와의 연결에 실패하였거나, 올바른 ICT 서버가 아닙니다.");
                            }
                        }

                        default -> {
                            printMessage("error", "잘못된 값을 입력하셨습니다.");
                            pause(2000);
                            continue;
                        }
                    }
                } while (true);
            }
        } while (true);
    }

    private static void changeFromICT(JSONObject updaterdata, String targetICTName, String targetKey, String targetValue) {
        try {
            JSONArray ictList = new JSONArray(updaterdata.get("ICT").toString());
            for (Object o : ictList) {
                JSONObject ict = new JSONObject(o.toString());
                if (ict.get("name").equals(targetICTName)) {
                    if (ict.isNull(targetKey)) throw new NumberFormatException();
                    ict.put(targetKey, targetValue);
                    // IctList에서 원래 ict 지우고
                    ictList.remove(findIndex(ictList, ict));
                    // 이름/URL 바꾼 새로운 ict 넣음
                    ictList.put(ict);
                    updaterdata.put("ICT", ictList);

                    File f = getContent("updater.dat", File.class);
                    FileWriter fw = new FileWriter(f);
                    fw.write(updaterdata.toString(4));
                    fw.flush();
                    fw.close();
                    return;
                }
            }
        } catch (NumberFormatException | JSONException e) {
            e.printStackTrace();
            printMessage("error", "ICT서버 리스트가 손상되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean alreadyExists(JSONArray ictArray, JSONObject object, String targetKey) {
        for (Object o : ictArray) {
            JSONObject obj = new JSONObject(o.toString());
            if (object.get(targetKey).equals(obj.get(targetKey))) return true;
        }
        return false;
    }

    public static int findIndex(JSONArray ictArray, JSONObject object) {
        int index = 0;
        for (Object o : ictArray) {
            JSONObject obj = new JSONObject(o.toString());
            if (obj.get("URL").equals(object.get("URL"))) return index;
            index++;
        }
        return -1;
    }
}
