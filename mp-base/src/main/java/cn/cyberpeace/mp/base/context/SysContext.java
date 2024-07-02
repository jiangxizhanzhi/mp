package cn.cyberpeace.mp.base.context;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName SysContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
