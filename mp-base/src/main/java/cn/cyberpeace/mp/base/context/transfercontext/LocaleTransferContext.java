package cn.cyberpeace.mp.base.context.transfercontext;

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
        LocaleContextHolder.setLocale(new Locale(value.getOrDefault(LANGUAGE_KEY, null),
                value.getOrDefault(COUNTRY_KEY, null), value.getOrDefault(VARIANT_KEY, null)));
    }
}
