 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DateTimeGenerator.java.
 // File size 502.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
