package cn.cyberpeace.mp.base.model.generator;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName UuidGenerator
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Generator
 */
@Component
public class UuidGenerator implements Generator {
    /**
     * generate
     *
     * @return
     */
    @Override
    public String generate() {
        return TextUtils.uuid();
    }
}
