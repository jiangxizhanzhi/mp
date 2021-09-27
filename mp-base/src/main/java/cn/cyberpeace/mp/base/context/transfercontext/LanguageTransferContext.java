package cn.cyberpeace.mp.base.context.transfercontext;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LanguageTransferContext implements TransferContext<String> {

    @Override
    public String getKey() {
        return "LANGUAGE";
    }

    @Override
    public String getValue() {
        return LocaleContextHolder.getLocale().getLanguage();
    }

    @Override
    public void setValue(String value) {
        LocaleContextHolder.setLocale(new Locale(value));
    }
}
