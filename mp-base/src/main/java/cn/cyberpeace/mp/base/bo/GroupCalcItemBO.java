package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class GroupCalcItemBO<T1, T2> {
    private T1 target;
    private T2 calcSource;
}
