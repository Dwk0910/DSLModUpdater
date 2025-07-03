package kr.kro.dslofficial.func;

import kr.kro.dslofficial.ColorText;
import kr.kro.dslofficial.Util;

import org.json.JSONArray;
import org.json.JSONObject;

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
        clearConsole();
        printTitle("ICT서버 설정");
        System.out.println();

        initialize();
        JSONObject data = getContent("updater.dat", JSONObject.class);
        JSONArray ictList = parseStr(data.get("ICT").toString(), JSONArray.class);

        do {
            clearConsole();
            if (ictList.isEmpty()) printMessage("info", "ICT서버 리스트가 비어 있습니다. 추가하시려면 입력란에 add를 입력하십시오.");
            else printMessage("info", "ICT서버를 추가하시려면 입력란에 add를 입력하십시오.");

            int index = 1;
            for (Object o : ictList) {
                JSONObject obj = parseStr(o.toString(), JSONObject.class);
                if (!isValidICTObject(obj)) {
                    printMessage("error", "updater.dat 파일이 손상되었습니다.");
                    System.exit(0);
                } else {
                    System.out.println(index + ") " + ColorText.text(obj.get("name").toString(), "blue", "none", false, false, false) + " - " + ColorText.text(obj.get("URL").toString(), "yellow", "none", false, false, false));
                    index++;
                }
            }

            String input = input("관리할 서버의 번호를 입력하십시오. 또는 서버추가시, add를 입력해 주십시오. (뒤로가기: exit)");
            if (input.equalsIgnoreCase("exit")) break;

            if (input.equalsIgnoreCase("add")) {
                System.out.println();
                printTitle("ICT서버 추가");
                String url = input("추가할 ICT서버의 URL를 입력해 주세요. (뒤로가기: exit)");
                if (url.equalsIgnoreCase("exit")) continue;
                // TODO : ICT서버 URL확인 추가
            } else {
                try {
                    int selected = Integer.parseInt(input);
                    if (ictList.isNull(selected - 1)) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    printMessage("error", "잘못된 값을 입력하셨습니다.");
                }
            }
        } while (true);
    }
}
