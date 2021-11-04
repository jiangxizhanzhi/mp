package cn.cyberpeace.mp.base.context;

import lombok.Data;

import java.util.Map;

/**
 * @Description 系统请求的上下文环境变量，用于存储通用的上下文信息
 * @Author maojun
 * @Date 2021/8/31
 */
@Data
public class SysContext {

    /**
     * 调用链路id
     */
    private String callChainId;

    /**
     * 透传内容
     */
    private Map<String, Object> passThroughMap;

}
