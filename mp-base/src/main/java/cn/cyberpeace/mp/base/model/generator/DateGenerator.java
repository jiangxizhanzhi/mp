package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DateGenerator implements Generator {
    @Override
    public LocalDate generate() {
        return LocalDate.now();
    }
}
