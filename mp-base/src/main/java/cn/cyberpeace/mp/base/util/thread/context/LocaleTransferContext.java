package cn.cyberpeace.mp.base.util.thread.context;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class LocaleTransferContext implements TransferContext<Locale> {
    @Override
    public Locale getValue() {
        return LocaleContextHolder.getLocale();
    }

    @Override
    public void setValue(Locale value) {
        LocaleContextHolder.setLocale(value);
    }
}
