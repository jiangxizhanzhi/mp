package cn.cyberpeace.mp.collection.core.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ListObjResult<T> {
    private List<T> list;

    private int total;

    public ListObjResult() {
        this.list = new ArrayList<>();
        this.total = 0;
    }
}
