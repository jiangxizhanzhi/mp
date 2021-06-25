package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListObjResult<T> extends BaseObjResult {

    @Builder.Default
    private List<Object> data = new ArrayList<>();

    @Builder.Default
    private Integer total = 0;

    @JsonIgnore
    @Builder.Default
    private List<T> objs = new ArrayList<>();

    public ListObjResult(List<T> objs, int total) {
        this();
        setObjs(objs);
        setTotal(total);
    }

    public ListObjResult(List<T> objs, Integer total, DynamicFields fields) {
        this();
        setObjs(objs);
        setTotal(Objects.nonNull(total) ? total : 0);
        setFields(fields);
    }

    @Override
    public ListObjResult<T> asResult() {
        data.clear();
        objs.forEach(obj -> data.add(objToResult(obj, getFields())));
        return this;
    }
}
