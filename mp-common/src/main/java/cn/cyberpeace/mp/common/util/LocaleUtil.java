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
 **/
public class LocaleUtil {

    public static boolean chineseLocalFlag() {
        Locale locale = LocaleContextHolder.getLocale();
        return StringUtils.isBlank(locale.getLanguage()) || Locale.CHINA.getLanguage().equals(locale.getLanguage());
    }
}
