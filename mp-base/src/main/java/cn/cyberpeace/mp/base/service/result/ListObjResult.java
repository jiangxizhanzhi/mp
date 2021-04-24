package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.service.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListObjResult<T> extends BaseResult {

    private List<Object> data = new ArrayList<>();

    private Integer total = 0;

    @JsonIgnore
    private List<T> objs = new ArrayList<>();

    public ListObjResult(List<T> objs, Integer total) {
        setObjs(objs);
        setTotal(total);
    }

    public ListObjResult(List<T> objs, Integer total, DynamicFields fields) {
        setObjs(objs);
        setTotal(total);
        setFields(fields);
    }

    @Override
    public ListObjResult<T> asResult() {
        data.clear();
        objs.forEach(obj -> data.add(objToResult(obj, getFields())));
        return this;
    }
}
