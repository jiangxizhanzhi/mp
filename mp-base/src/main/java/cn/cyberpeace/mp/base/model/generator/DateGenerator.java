 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DateGenerator.java.
 // File size 482.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
