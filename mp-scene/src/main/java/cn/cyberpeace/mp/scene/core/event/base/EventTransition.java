package cn.cyberpeace.mp.scene.core.event.base;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName EventTransition
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class EventTransition {
    public static String genKey(int event, int eventStatus) {
        return StringUtils.joinWith("-", event, eventStatus);
    }
}
