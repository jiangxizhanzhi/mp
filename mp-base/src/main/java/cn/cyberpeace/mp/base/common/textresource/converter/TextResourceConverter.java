package cn.cyberpeace.mp.base.common.textresource.converter;


public interface TextResourceConverter<T> {

    default T convert(String value) {
        return (T) value;
    }

    default String reconvert(T value) {
        return value == null ? null : value.toString();
    }

}
