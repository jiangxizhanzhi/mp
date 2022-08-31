package cn.cyberpeace.mp.base.common.textresource.converter;

import cn.cyberpeace.mp.base.common.TimeCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class TextResourceDateConverter implements TextResourceConverter<LocalDate> {

    @Override
    public LocalDate convert(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        try {
            return TimeCommon.stringToLocalDate(value);
        } catch (Exception e) {
            log.error("Convert {} to local date error: {}", value, e.getMessage());
            return null;
        }
    }

    @Override
    public String reconvert(LocalDate value) {
        return value == null ? null : TimeCommon.localDateToString(value);
    }

}
