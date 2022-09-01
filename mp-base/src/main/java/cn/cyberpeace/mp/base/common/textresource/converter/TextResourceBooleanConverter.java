package cn.cyberpeace.mp.base.common.textresource.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

@Component
public class TextResourceBooleanConverter implements TextResourceConverter<Boolean> {

    @Override
    public Boolean convert(String value) {
        return BooleanUtils.toBoolean(value);
    }

}
