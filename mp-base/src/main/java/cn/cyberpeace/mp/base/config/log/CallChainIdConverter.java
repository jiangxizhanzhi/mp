 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CallChainIdConverter.java.
 // File size 398.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.config.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.cyberpeace.mp.base.context.SysContextHolder;

public class CallChainIdConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return SysContextHolder.getCallChainId();
    }
}
