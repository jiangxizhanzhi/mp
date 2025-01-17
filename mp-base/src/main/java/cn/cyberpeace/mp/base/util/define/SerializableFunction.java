 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SerializableFunction.java.
 // File size 195.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.define;

import java.io.Serializable;

@FunctionalInterface
public interface SerializableFunction<T> extends Serializable {
    Object get(T source);
}
