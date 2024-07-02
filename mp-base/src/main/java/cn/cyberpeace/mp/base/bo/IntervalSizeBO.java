package cn.cyberpeace.mp.base.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName IntervalSizeBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Setter
@Getter
public class IntervalSizeBO<T, S extends Number> {
    private T start;
    private T end;
    private S size;

    public IntervalSizeBO(T start, T end, S size) {
        this.start = start;
        this.end = end;
        this.size = size;
    }
}
