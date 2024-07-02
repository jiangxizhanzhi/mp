package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * @ClassName TimeGenerator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Generator
 */
@Component
public class TimeGenerator implements Generator {
    /**
     * generate
     *
     * @return
     */
    @Override
    public LocalTime generate() {
        return LocalTime.now();
    }
}
