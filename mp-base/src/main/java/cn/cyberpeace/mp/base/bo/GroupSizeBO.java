package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class GroupSizeBO<T, S extends Number> {
    private T target;
    private S size;
}
