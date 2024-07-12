 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventTransition.java.
 // File size 384.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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
