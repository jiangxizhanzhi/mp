package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;

import java.lang.reflect.Field;
import java.util.List;

public interface CloudHttpParam extends HttpParam {
    @Override
    default String toPathString() {
        List<Field> fieldList = BeanUtils.getFieldsByObject(this);
        return BeanUtils.wrapperGetParamsByFieldsAndObject(fieldList, this, Boolean.TRUE);
    }
}
