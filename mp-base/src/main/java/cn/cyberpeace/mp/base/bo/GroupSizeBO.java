package cn.cyberpeace.mp.base.bo;

import lombok.Data;

/**
 * @ClassName GroupSizeBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class GroupSizeBO<T, S extends Number> {
    private T target;
    private S size;
}
