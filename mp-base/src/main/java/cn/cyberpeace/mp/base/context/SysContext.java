package cn.cyberpeace.mp.base.context;

import lombok.Data;

import java.util.Map;

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
