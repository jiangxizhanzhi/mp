package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class IntervalSizeBO<T> {
    private T start;
    private T end;
    private Long size;
}
