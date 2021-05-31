package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateGenerator implements Generator {
    @Override
    public LocalDate generate() {
        return LocalDate.now();
    }
}
