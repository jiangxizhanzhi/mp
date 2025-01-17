 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DateTimeGenerator.java.
 // File size 305.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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
