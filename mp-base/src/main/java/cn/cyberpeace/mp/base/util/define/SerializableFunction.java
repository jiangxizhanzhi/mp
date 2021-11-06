package cn.cyberpeace.mp.base.util.define;

import java.io.Serializable;

@FunctionalInterface
public interface SerializableFunction<T> extends Serializable {
    Object get(T source);
}
