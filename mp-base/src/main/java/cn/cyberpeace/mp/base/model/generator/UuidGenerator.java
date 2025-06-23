 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UuidGenerator.java.
 // File size 501.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
