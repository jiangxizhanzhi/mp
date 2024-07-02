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
