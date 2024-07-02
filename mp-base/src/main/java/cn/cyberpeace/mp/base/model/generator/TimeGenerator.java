package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

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
