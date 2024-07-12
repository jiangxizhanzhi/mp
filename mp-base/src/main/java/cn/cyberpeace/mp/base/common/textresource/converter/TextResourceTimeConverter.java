 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceTimeConverter.java.
 // File size 1233.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common.textresource.converter;

import cn.cyberpeace.mp.base.common.TimeCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * @ClassName TextResourceTimeConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TextResourceConverter
 */
@Slf4j
@Component
public class TextResourceTimeConverter implements TextResourceConverter<LocalTime> {

    /**
     * convert
     *
     * @param value
     * @return
     */
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

    /**
     * reconvert
     *
     * @param value
     * @return
     */
    @Override
    public String reconvert(LocalTime value) {
        return value == null ? null : TimeCommon.localTimeToString(value);
    }

}
