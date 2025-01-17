 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UuidGenerator.java.
 // File size 308.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
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
