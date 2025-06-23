 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TextResourceConverter.java.
 // File size 652.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.common.textresource.converter;

/**
 * @ClassName TextResourceConverter
 * @Description 值转换接口，用于定义文本资源和对象字段的转换方法
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface TextResourceConverter<T> {

    /**
     * convert
     *
     * @param value
     * @return
     */
    default T convert(String value) {
        return (T) value;
    }

    /**
     * reconvert
     *
     * @param value
     * @return
     */
    default String reconvert(T value) {
        return value == null ? null : value.toString();
    }

}
