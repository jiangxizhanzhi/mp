package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;

import java.lang.reflect.Field;
import java.util.List;

public interface CloudHttpParam extends HttpParam {
    @Override
    default String toPathString() {
        List<Field> fieldList = ParamUtils.getFieldsByObject(this);
        return ParamUtils.wrapperGetParamsByFieldsAndObject(fieldList, this, Boolean.TRUE);
    }
}
