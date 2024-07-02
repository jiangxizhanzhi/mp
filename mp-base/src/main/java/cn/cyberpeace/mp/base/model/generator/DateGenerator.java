package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @ClassName DateGenerator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Generator
 */
@Component
public class DateGenerator implements Generator {
    /**
     * generate
     *
     * @return
     */
    @Override
    public LocalDate generate() {
        return LocalDate.now();
    }
}
