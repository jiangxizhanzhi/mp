package cn.cyberpeace.mp.base.util;

import java.io.Serializable;

@FunctionalInterface
public interface SerializableFunction<T> extends Serializable {
    Object get(T source);
}
