package cn.cyberpeace.mp.base.context.transfercontext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/**
 * @ClassName LocaleTransferContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TransferContext
 */
@Component
public class LocaleTransferContext implements TransferContext<Map<String, String>> {

    public static final String LANGUAGE_KEY = "LANGUAGE";

    public static final String COUNTRY_KEY = "COUNTRY";

    public static final String VARIANT_KEY = "VARIANT";

    /**
     * getKey
     *
     * @return
     */
    @Override
    public String getKey() {
        return "LOCALE";
    }

    /**
     * getValue
     *
     * @return
     */
    @Override
    public Map<String, String> getValue() {
        Locale locale = LocaleContextHolder.getLocale();
        return Map.of(LANGUAGE_KEY, locale.getLanguage(), COUNTRY_KEY, locale.getCountry(), VARIANT_KEY,
                locale.getVariant());
    }

    /**
     * setValue
     *
     * @param value
     */
    @Override
    public void setValue(Map<String, String> value) {
        LocaleContextHolder.setLocale(new Locale(value.getOrDefault(LANGUAGE_KEY, StringUtils.EMPTY),
                value.getOrDefault(COUNTRY_KEY, StringUtils.EMPTY), value.getOrDefault(VARIANT_KEY, StringUtils.EMPTY)));
    }
}
