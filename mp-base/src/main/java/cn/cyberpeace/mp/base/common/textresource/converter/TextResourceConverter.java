package cn.cyberpeace.mp.base.common.textresource.converter;

/**
 * 值转换接口，用于定义文本资源和对象字段的转换方法
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
