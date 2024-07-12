 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocaleUtil.java.
 // File size 587.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Objects;

/**
 * @ClassName LocaleUtil
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/25 14:13
 * @Version 1.0.0
 */
public class LocaleUtil {

    public static boolean chineseLocalFlag() {
        Locale locale = LocaleContextHolder.getLocale();
        return StringUtils.isBlank(locale.getLanguage()) || Locale.CHINA.getLanguage().equals(locale.getLanguage());
    }
}
