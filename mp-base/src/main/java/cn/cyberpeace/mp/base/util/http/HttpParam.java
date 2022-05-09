package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.hutool.core.util.URLUtil;

import java.nio.charset.StandardCharsets;

/**
 * http查询参数，实现该接口定义对应bean
 */
public interface HttpParam {

    static String toPathString(Object httpParam) {
        return URLUtil.buildQuery(BeanUtils.getMap(httpParam), StandardCharsets.UTF_8);
    }

    /**
     * 转换为Url参数
     */
    default String toPathString() {
        return toPathString(this);
    }

}
