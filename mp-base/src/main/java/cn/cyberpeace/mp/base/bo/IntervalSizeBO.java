package cn.cyberpeace.mp.base.bo;

import lombok.Getter;
import lombok.Setter;

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
