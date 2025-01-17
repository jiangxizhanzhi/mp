 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DateGenerator.java.
 // File size 289.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.model.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateGenerator implements Generator {
    @Override
    public LocalDate generate() {
        return LocalDate.now();
    }
}
