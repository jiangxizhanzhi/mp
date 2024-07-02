package cn.cyberpeace.mp.base.common.textresource.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName TextResourceIntegerConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TextResourceConverter
 */
@Component
public class TextResourceIntegerConverter implements TextResourceConverter<Integer> {

    /**
     * convert
     *
     * @param value
     * @return
     */
    @Override
    public Integer convert(String value) {
        return StringUtils.isNumeric(value) ? Integer.parseInt(value) : null;
    }

}
