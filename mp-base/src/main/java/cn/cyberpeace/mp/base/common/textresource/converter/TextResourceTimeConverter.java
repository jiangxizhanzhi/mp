 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceTimeConverter.java.
 // File size 915.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common.textresource.converter;

import cn.cyberpeace.mp.base.common.TimeCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
@Component
public class TextResourceTimeConverter implements TextResourceConverter<LocalTime> {

    @Override
    public LocalTime convert(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        try {
            return TimeCommon.stringToLocalTime(value);
        } catch (Exception e) {
            log.error("Convert {} to local time error: {}", value, e.getMessage());
            return null;
        }
    }

    @Override
    public String reconvert(LocalTime value) {
        return value == null ? null : TimeCommon.localTimeToString(value);
    }

}
