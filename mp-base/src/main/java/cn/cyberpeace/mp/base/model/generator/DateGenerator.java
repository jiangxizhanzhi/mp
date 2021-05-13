package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateGenerator implements Generator {
    @Override
    public Date generate() {
        return new Date();
    }
}
