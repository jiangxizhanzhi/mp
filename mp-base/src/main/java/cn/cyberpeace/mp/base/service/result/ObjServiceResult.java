package cn.cyberpeace.mp.base.service.result;

import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.code.ServiceCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ObjServiceResult<T> extends BaseServiceResult<ObjResult<T>, T> {

    /**
     * ObjServiceResult
     *
     * @param source
     */
    public ObjServiceResult(ObjResult<T> source) {
        super(source);
    }

    /**
     * ObjServiceResult
     *
     * @param source
     * @param code
     */
    public ObjServiceResult(ObjResult<T> source, ServiceCode code) {
        super(source, code);
    }

    /**
     * ObjServiceResult
     *
     * @param source
     * @param code
     * @param detail
     */
    public ObjServiceResult(ObjResult<T> source, ServiceCode code, String detail) {
        super(source, code, detail);
    }

    /**
     * ObjServiceResult
     *
     * @param obj
     */
    public ObjServiceResult(T obj) {
        this(new ObjResult<>(obj));
    }

    /**
     * ObjServiceResult
     *
     * @param obj
     * @param fields
     */
    public ObjServiceResult(T obj, DynamicFields fields) {
        this(new ObjResult<>(obj, fields));
    }

    /**
     * sourceToData
     *
     * @param source
     * @return
     */
    @Override
    public T sourceToData(ObjResult<T> source) {
        if (source == null) {
            return null;
        }

        return source.asResult();
    }

    /**
     * asResult
     *
     * @return
     */
    @Override
    public ObjServiceResult<T> asResult() {
        super.asResult();
        return this;
    }

    /**
     * sourceObj
     *
     * @return
     */
    public T sourceObj() {
        ObjResult<T> source = getSource();
        if (source == null) {
            return null;
        }
        return source.getObj();
    }

}
