package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class GroupCountBO<T> {
    private T target;
    private Integer count;
}
