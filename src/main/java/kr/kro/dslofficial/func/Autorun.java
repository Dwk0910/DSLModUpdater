package kr.kro.dslofficial.func;

import kr.kro.dslofficial.Util;
import org.json.JSONObject;
import org.json.JSONArray;

public class Autorun {
    public Autorun() {
        try {
            JSONObject obj = Util.getContent("updater.dat", JSONObject.class);
            if (obj.get("ICT") == null) {
                Util.printMessage("error", "updater.dat파일이 손상되었습니다. 프로세스를 종료합니다.");
                Thread.sleep(1000);
                System.exit(-1);
            } else {
                JSONArray ictList = Util.parseStr(obj.get("ICT").toString(), JSONArray.class);

                if (ictList.isEmpty()) {
                    System.out.println("ICT서버가 없습니다. ");
                } else {
                    for (Object o : ictList) {
                        System.out.println(o.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
