package cn.cyberpeace.mp.cscene.constant;

import cn.cyberpeace.mp.cscene.bo.CsceneBOExt;

public class ControllerConstant {

    @SuppressWarnings("java:S1075")
    public static final String BASE_API_PATH = "/cscene";

    private ControllerConstant() {
    }

    public static CsceneBOExt csceneBOExt = new CsceneBOExt();
}
