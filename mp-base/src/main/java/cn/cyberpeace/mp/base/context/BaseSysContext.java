package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.util.IdGenerator;

import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/8/31
 */
public class BaseSysContext implements SysContext {
    private String callChainId;

    private Map<String, Object> passThroughMap;

    public BaseSysContext() {
        this.callChainId = IdGenerator.generateUUID();
    }

    @Override
    public String getCallChainId() {
        return callChainId;
    }

    @Override
    public void setCallChainId(String callChainId) {
        this.callChainId = callChainId;
    }

    @Override
    public void setPassThroughMap(Map<String, Object> map) {
        this.passThroughMap = map;
    }

    @Override
    public Map<String, Object> getPassThroughMap() {
        return this.passThroughMap;
    }
}