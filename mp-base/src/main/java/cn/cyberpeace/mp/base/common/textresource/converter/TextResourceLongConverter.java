 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceLongConverter.java.
 // File size 402.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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
