package cn.cyberpeace.mp.sceneplugincloud.core.common;

import cn.cyberpeace.mp.base.util.BeanUtils;

public interface HttpParam {

    default String toPathString() {
        return BeanUtils.wrapperGetParamsByObject(this);
    }

}
