 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SerializableFunction.java.
 // File size 415.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.define;

import java.io.Serializable;

/**
 * @ClassName SerializableFunction
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@FunctionalInterface
public interface SerializableFunction<T> extends Serializable {
    /**
     * get
     *
     * @param source
     * @return
     */
    Object get(T source);
}
