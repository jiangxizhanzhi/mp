package cn.cyberpeace.mp.base.bo;

import lombok.Data;

@Data
public class GroupSizeBO<T> {
    private T target;
    private Long size;
}
