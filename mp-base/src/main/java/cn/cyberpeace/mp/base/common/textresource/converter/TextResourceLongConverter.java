package cn.cyberpeace.mp.base.common.textresource.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName TextResourceLongConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TextResourceConverter
 */
@Component
public class TextResourceLongConverter implements TextResourceConverter<Long> {

    /**
     * convert
     *
     * @param value
     * @return
     */
    @Override
    public Long convert(String value) {
        return StringUtils.isNumeric(value) ? Long.parseLong(value) : null;
    }

}
