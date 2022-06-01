package cn.cyberpeace.mp.devops.cluster.vo.common;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class PageResult<T> extends ListObjServiceResult<T> {

    public PageResult(List<T> list, int total) {
        super(new ListObjResult<>(list,total));
        this.asResult();
    }

    @Override
    public ListObjResult<T> sourceToData(ListObjResult<T> source) {
        if (source == null) {
            return null;
        }

        source.asResult();
        return source;
    }

    @Override
    public PageResult<T> asResult() {
        super.asResult();
        return this;
    }
}
