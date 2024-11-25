package cn.cyberpeace.mp.cscene.controller.collect;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 一行数据对应多条信息
 * @param <T>
 */
@Getter
@Setter
public class OneRowDataWrapper<T> {
    private List<T> modifiedPrev;
    private List<T> added;
    private List<T> values;
    private List<T> modified;
    private List<T> deletedPrev;
}
