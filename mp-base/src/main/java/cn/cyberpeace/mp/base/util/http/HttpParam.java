package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.mptool.URLUtil;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName HttpParam
 * @Description http查询参数，实现该接口定义对应bean
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface HttpParam {

    /**
     * toPathString
     *
     * @param httpParam
     * @return
     */
    static String toPathString(Object httpParam) {
        return URLUtil.buildQuery(BeanUtils.getMap(httpParam), StandardCharsets.UTF_8);
    }

    /**
     * 转换为Url参数
     *
     * @return
     */
    default String toPathString() {
        return toPathString(this);
    }

}
