package cn.cyberpeace.mp.scene.core.event.base;

import org.apache.commons.lang3.StringUtils;

public class EventTransition {
    public static String genKey(int event, int eventStatus) {
        return StringUtils.joinWith("-", event, eventStatus);
    }
}
