package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.cscene.bo.CsceneBO;

public class CsceneLegalUtils {
    public static boolean isEmptyConfig(CsceneBO csceneBO) {
        return csceneBO == null || csceneBO.getSceneConfig() == null;
    }

    public static boolean isEmptyTopology(CsceneBO csceneBO) {
        return isEmptyConfig(csceneBO) || csceneBO.getSceneConfig().getTopology() == null;
    }

    public static boolean isEmptyElements(CsceneBO csceneBO) {
        if (isEmptyTopology(csceneBO)) {
            return true;
        }
        var elements = csceneBO.getSceneConfig().getTopology().getElements();
        return elements == null || elements.size() == 0;
    }
}
