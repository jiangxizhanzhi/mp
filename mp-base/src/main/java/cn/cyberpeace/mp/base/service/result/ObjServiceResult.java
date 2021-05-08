package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ObjServiceResult<T> extends ServiceResult<ObjResult<T>> {

    public ObjServiceResult(ObjResult<T> source) {
        super(source);
    }

    public ObjServiceResult(ObjResult<T> source, ServiceCode code) {
        super(source, code);
    }

    public ObjServiceResult(ObjResult<T> source, ServiceCode code, String detail) {
        super(source, code, detail);
    }

    @Override
    public ObjServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

}
