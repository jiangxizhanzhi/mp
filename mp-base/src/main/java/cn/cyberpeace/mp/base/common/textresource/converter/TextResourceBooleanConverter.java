package cn.cyberpeace.mp.base.common.textresource.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName TextResourceBooleanConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TextResourceConverter
 */
@Component
public class TextResourceBooleanConverter implements TextResourceConverter<Boolean> {

    /**
     * convert
     *
     * @param value
     * @return
     */
    @Override
    public Boolean convert(String value) {
        return BooleanUtils.toBoolean(value);
    }

}
