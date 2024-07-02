package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName DateTimeGenerator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Generator
 */
@Component
public class DateTimeGenerator implements Generator {
    /**
     * generate
     *
     * @return
     */
    @Override
    public LocalDateTime generate() {
        return LocalDateTime.now();
    }
}
