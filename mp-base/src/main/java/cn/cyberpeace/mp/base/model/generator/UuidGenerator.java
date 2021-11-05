package cn.cyberpeace.mp.base.model.generator;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import org.springframework.stereotype.Component;

@Component
public class UuidGenerator implements Generator {
    @Override
    public String generate() {
        return TextUtils.uuid();
    }
}
