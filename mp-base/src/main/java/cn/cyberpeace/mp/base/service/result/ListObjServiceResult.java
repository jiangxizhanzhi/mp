package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ListObjServiceResult<T> extends ServiceResult<ListObjResult<T>> {

    public ListObjServiceResult(ListObjResult<T> source) {
        super(source);
    }

    public ListObjServiceResult(ListObjResult<T> source, ServiceCode code) {
        super(source, code);
    }

    public ListObjServiceResult(ListObjResult<T> source, ServiceCode code, String detail) {
        super(source, code, detail);
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
    public ListObjServiceResult<T> asResult() {
        super.asResult();
        return this;
    }
}
