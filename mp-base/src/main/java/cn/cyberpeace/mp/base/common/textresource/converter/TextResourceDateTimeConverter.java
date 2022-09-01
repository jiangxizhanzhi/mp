package cn.cyberpeace.mp.base.common.textresource.converter;

import cn.cyberpeace.mp.base.common.TimeCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TextResourceDateTimeConverter implements TextResourceConverter<LocalDateTime> {

    @Override
    public LocalDateTime convert(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        try {
            return TimeCommon.stringToLocalDateTime(value);
        } catch (Exception e) {
            log.error("Convert {} to local date time error: {}", value, e.getMessage());
            return null;
        }
    }

    @Override
    public String reconvert(LocalDateTime value) {
        return value == null ? null : TimeCommon.localDateTimeToString(value);
    }

}
