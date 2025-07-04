package kr.kro.dslofficial.func;

import kr.kro.dslofficial.ColorText;
import kr.kro.dslofficial.Util;
import kr.kro.dslofficial.obj.ServerResponse;
import kr.kro.dslofficial.obj.enums.ConnectionStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Autorun extends Util {
    /*
    TODO: 아래거 전부 다~~
    1. JSONObject data를 통해 기본 ICT 불러오기
    1.1. 기본 ICT가 없다면? -> ICT리스트 불러와서 선택하라고 하기
    1.2. 이 리스트마저도 없다면? -> 등록된 ICT가 없으므로, 자동 업데이트를 실행할 수 없습니다. 종료합니다.
    2. 자동 업데이터가 기본 ICT(XXXX)로 검사를 시작할 것입니다. 다른 ICT로 검사하거나, 검사를 건너뛰실 경우 1초 이내에 ENTER를 눌러 주십시오....
    2.1. 상세 :
        [INFO] 검사할 ICT서버를 선택하십시오. 검사를 건너뛰려면 입력란에 skip을 입력하고 <ENTER>를 누르십시오.
        1) DCS
        2) PLAYDSL
        3) ...
        4) ...
        ====== 메뉴를 선택해 주세요 ==================================
        skip

        2.1.1. skip -> [INFO] 모드 검사를 건너 뛰셨습니다. 자동 업데이터를 종료합니다.
        2.1.2. 번호선택 -> [INFO] 선택 ICT서버 : DCS
    3. [INFO] 검사를 시작합니다.
    4. ICT서버에 연결을 시도합니다... 완료
    5. ICT서버에서 모드 리스트를 불러오는 중입니다... 완료
    6. 모드의 적합성을 검사중입니다... 완료
    6.1. 올바른가? -> Latest Version (25APR16.01) detected. 설치된 모드가 올바릅니다. 자동 업데이터를 종료합니다.
    6.2. 올바르지않은가? -> 모드가 서버와 올바르지 않습니다.
         새로운 모드 다운로드를 시도하시겠습니까? [Y\N]
         6.2.1. Y -> TODO : 이거 만들기
                N -> 모드 다운로드를 취소하셨습니다. 자동 업데이터를 종료합니다.
     */
    public Autorun() {
        // initialize
        JSONObject data = getContent("updater.dat", JSONObject.class);
        JSONObject defaultICT = parseStr(data.get("default").toString(), JSONObject.class);
        JSONArray ictList = parseStr(data.get("ICT").toString(), JSONArray.class);

        System.out.println();
        printTitle("DSLModUpdater AGENT AUTORUN");
        System.out.println();

        JSONObject selectedICT = null;
        if (!defaultICT.isNull("name")) {
            printMessage("info", "기본 ICT서버를 찾았습니다. (" + defaultICT.getString("name") + ")");
            if (waitInput("다른 ICT서버로 검사하시거나, 검사를 건너뛰시려면 1초 이내에 <ENTER>를 눌러 주십시오...", 1000) == null)
                selectedICT = defaultICT;
        }

        // defaultICT가 없거나, 수동으로 ICT를 설정하려는 경우, 검사를 건너뛰려는 경우
        if (selectedICT == null) {
            // 등록된 ICT서버 없음
            if (ictList.isEmpty()) {
                printMessage("error", "등록된 ICT서버가 없어 자동검사를 진행할 수 없습니다.");
                printMessage("error", System.getProperty("user.home") + File.separator + "Documents" + File.separator + "DSLModUpdater" + File.separator + "run.bat 파일을 통해 일반모드로 업데이터를 키신 다음, ICT서버를 추가하여 주십시오.");
                pause(3000);
                return;
            }

            // ICT선택창 출력
            printMessage("info", "검사를 원하는 ICT서버를 선택하십시오. 검사를 건너뛰시려면 입력란에 skip을 입력하고 <ENTER>를 누르십시오.");
            System.out.println();
            int index = 1;
            for (Object o : ictList) {
                JSONObject obj;
                try {
                     obj = new JSONObject(o.toString());
                     if (obj.isNull("name") || obj.isNull("URL")) throw new Exception();
                } catch (Exception e) {
                    printMessage("error", "updater.dat 파일이 손상되었습니다.");
                    pause(3000);
                    return;
                }

                System.out.println(ColorText.text(index + ") ", "yellow", "none", true, false, false) + ColorText.text(obj.getString("name"), "white", "none", false, false, false));
                index++;
            }

            do {
                System.out.println();
                String input = input("검사할 ICT서버를 선택해 주십시오. (건너뛰기: skip)");
                if (input.equals("skip")) {
                    printMessage("info", "검사를 건너뜁니다...");
                    return;
                } else {
                    try {
                        int selected = Integer.parseInt(input);
                        if (ictList.isNull(selected - 1)) throw new NumberFormatException();
                        else {
                            selectedICT = new JSONObject(ictList.get(selected - 1).toString());
                            break;
                        }
                    } catch (NumberFormatException e) {
                        printMessage("error", "잘못된 값을 입력하셨습니다.");
                        continue;
                    } catch (JSONException e) {
                        printMessage("error", "updater.dat 파일이 손상되었습니다.");
                        pause(3000);
                        return;
                    }
                }
            } while (true);
        }

        printMessage("info", ColorText.text(" [ " + selectedICT.getString("name") + " ] ", "green", "none", false, false, false) + ColorText.text(" ICT 서버로 검사를 시작합니다.", "white", "none", true, false, false));

        // ICT Server Validation
        printMessage("info", "서버와 통신을 시도중입니다.");
        ServerResponse resp = getResponse(selectedICT.getString("URL"));
        if (resp.status != ConnectionStatus.OK) {
            printMessage("error", "서버와의 통신에 실패하였습니다.");
            pause(3000);
            return;
        }

        JSONObject obj;
        JSONArray serverMods;
        try {
            obj = new JSONObject(resp.response);
            if (obj.isNull("version") || obj.isNull("mods"))
                throw new JSONException("");
            else serverMods = new JSONArray(obj.get("mods").toString());
        // 여기서 catch되는 JSONException은 아래와 다르게 처리해야함
        } catch (JSONException e) {
            printMessage("error", "올바른 ICT서버가 아닙니다.");
            pause(2000);
            return;
        }

        try {
            File modsDir = new File(data.getString("path"));
            if (!modsDir.exists()) {
                printMessage("error", "모드 폴더 (" + modsDir.toPath() + ") 를 찾을 수 없습니다.");
                pause(3000);
                return;
            }

            // 비교 시작
            boolean isValid = true;
            // Step 1. 파일 갯수가 동일하여야 함
            if (modsDir.listFiles() != null && Objects.requireNonNull(modsDir.listFiles()).length != serverMods.length()) isValid = false;
            // Step 2. Hash 값 비교
            else {
                Map<String, Boolean> localModMap = new HashMap<>();
                for (File f : Objects.requireNonNull(modsDir.listFiles())) {
                    localModMap.put(hashFile(f), false);
                }

                try {
                    for (Object o : serverMods) {
                        JSONObject mod = new JSONObject(o.toString());
                        if (localModMap.get(mod.getString("hash")) == null) {
                            isValid = false;
                            break;
                        }
                        else localModMap.put(mod.getString("hash"), true);
                    }

                    for (String key : localModMap.keySet()) {
                        if (!localModMap.get(key)) {
                            isValid = false;
                            break;
                        }
                    }
                // 다르게 catch 필요
                } catch (JSONException e) {
                    printMessage("error", "ICT서버 mods 배열에 오류가 있습니다.");
                    pause(3000);
                    return;
                }
            }

            printTitle("검사 완료");
            System.out.println();

            if (isValid) {
                printMessage("info", "모드가 최신 버전 (" + ColorText.text(obj.getString("version"), "green", "none", true, false, false) + ") 과 일치합니다.");
                pause(2000);
            } else {
                printMessage("warn", "모드가 최신 버전과 상이합니다.");
                if (ask("새로운 모드를 다운받고 적용하시겠습니까?\n" + ColorText.text("mods폴더 (" + modsDir.toPath() + ") 안의 모든 내용이 손실됩니다!", "red", "none", true, false, false))) {
                    //TODO : APPLY MODS
                    System.out.println("새로운모드적용");
                }
            }
        } catch (JSONException e) {
            printMessage("error", "updater.dat 파일이 손상되었습니다.");
            pause(3000);
        }
    }
}