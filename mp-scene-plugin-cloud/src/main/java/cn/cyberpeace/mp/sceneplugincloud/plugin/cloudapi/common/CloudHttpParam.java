package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.base.util.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;

public interface CloudHttpParam extends HttpParam {
    @Override
    default String toPathString() {
        List<Field> fieldList = BeanUtils.getFieldsByObject(this);
        return BeanUtils.wrapperGetParamsByFieldsAndObject(fieldList, this, Boolean.TRUE);
    }
}
