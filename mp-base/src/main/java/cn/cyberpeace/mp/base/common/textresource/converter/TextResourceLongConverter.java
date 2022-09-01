package cn.cyberpeace.mp.base.common.textresource.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TextResourceLongConverter implements TextResourceConverter<Long> {

    @Override
    public Long convert(String value) {
        return StringUtils.isNumeric(value) ? Long.parseLong(value) : null;
    }

}
