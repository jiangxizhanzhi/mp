package cn.cyberpeace.mp.sceneplugincloud.util.http;

import cn.cyberpeace.mp.base.util.http.ParamUtils;

public interface HttpParam {

    default String toPathString() {
        return ParamUtils.wrapperGetParamsByObject(this);
    }

}
