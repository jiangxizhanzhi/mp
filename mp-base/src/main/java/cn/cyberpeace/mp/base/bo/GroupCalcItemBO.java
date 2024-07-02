package cn.cyberpeace.mp.base.bo;

import lombok.Data;

/**
 * @ClassName GroupCalcItemBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class GroupCalcItemBO<T1, T2> {
    private T1 target;
    private T2 calcSource;
}
