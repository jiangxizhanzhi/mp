 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CallChainIdConverter.java.
 // File size 625.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextHolder;

/**
 * @ClassName CallChainIdConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ClassicConverter
 */
public class CallChainIdConverter extends ClassicConverter {
    /**
     * convert
     *
     * @param event
     * @return
     */
    @Override
    public String convert(ILoggingEvent event) {
        return SysContextHolder.getCallChainId();
    }
}
