package cn.cyberpeace.mp.base.common.textresource.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TextResourceIntegerConverter implements TextResourceConverter<Integer> {

    @Override
    public Integer convert(String value) {
        return StringUtils.isNumeric(value) ? Integer.parseInt(value) : null;
    }

}
