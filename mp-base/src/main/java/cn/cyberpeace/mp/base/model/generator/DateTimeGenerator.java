package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeGenerator implements Generator {
    @Override
    public LocalDateTime generate() {
        return LocalDateTime.now();
    }
}
