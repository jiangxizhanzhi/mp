 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceBooleanConverter.java.
 // File size 382.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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
