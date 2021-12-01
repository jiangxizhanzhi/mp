package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class IntervalSizeBO<T, S extends Number> {
    private T start;
    private T end;
    private S size;
}
