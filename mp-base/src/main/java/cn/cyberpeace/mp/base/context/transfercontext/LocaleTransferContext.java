 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocaleTransferContext.java.
 // File size 1203.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.context.transfercontext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class LocaleTransferContext implements TransferContext<Map<String, String>> {

    public static final String LANGUAGE_KEY = "LANGUAGE";

    public static final String COUNTRY_KEY = "COUNTRY";

    public static final String VARIANT_KEY = "VARIANT";

    @Override
    public String getKey() {
        return "LOCALE";
    }

    @Override
    public Map<String, String> getValue() {
        var locale = LocaleContextHolder.getLocale();
        return Map.of(LANGUAGE_KEY, locale.getLanguage(), COUNTRY_KEY, locale.getCountry(), VARIANT_KEY,
                locale.getVariant());
    }

    @Override
    public void setValue(Map<String, String> value) {
        LocaleContextHolder.setLocale(new Locale(value.getOrDefault(LANGUAGE_KEY, StringUtils.EMPTY),
                value.getOrDefault(COUNTRY_KEY, StringUtils.EMPTY), value.getOrDefault(VARIANT_KEY, StringUtils.EMPTY)));
    }
}
