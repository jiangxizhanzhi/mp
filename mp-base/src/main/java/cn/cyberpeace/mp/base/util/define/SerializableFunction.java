package cn.cyberpeace.mp.base.util.define;

import java.io.Serializable;

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
