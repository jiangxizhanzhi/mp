package cn.cyberpeace.mp.base.context;

import lombok.Data;

import java.util.Map;

@Data
public class SysContext {

    
    private String callChainId;

    
    private Map<String, Object> passThroughMap;

}
