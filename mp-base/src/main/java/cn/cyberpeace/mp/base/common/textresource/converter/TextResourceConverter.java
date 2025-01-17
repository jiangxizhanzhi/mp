 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceConverter.java.
 // File size 383.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.common.textresource.converter;

/**
 * 值转换接口，用于定义文本资源和对象字段的转换方法
 */
public interface TextResourceConverter<T> {

    default T convert(String value) {
        return (T) value;
    }

    default String reconvert(T value) {
        return value == null ? null : value.toString();
    }

}
