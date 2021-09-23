package cn.cyberpeace.mp.base.context;

import java.util.Map;

/**
 * @Description 系统请求的上下文环境变量，用于存储通用的上下文信息
 * @Author maojun
 * @Date 2021/8/31
 */
public interface SysContext {
    /**
     * 查询调用链路id
     * @return
     */
    String getCallChainId();

    /**
     * 设置调用链路id
     * @return
     */
    void setCallChainId(String callChainId);

    /**
     * 用于透传内容
     * @param map
     */
    void setPassThroughMap(Map<String, Object> map);

    /**
     * 获取透传内容
     * @return
     */
    Map<String, Object> getPassThroughMap();
}