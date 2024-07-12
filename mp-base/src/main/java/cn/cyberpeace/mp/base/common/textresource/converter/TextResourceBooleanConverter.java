 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceBooleanConverter.java.
 // File size 622.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
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
