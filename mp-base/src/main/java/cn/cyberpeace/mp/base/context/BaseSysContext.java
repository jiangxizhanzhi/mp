package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.util.IdGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/8/31
 */
public class BaseSysContext implements SysContext {
    private String executeId;

    private Map<String, Object> map;

    public BaseSysContext() {
        this.executeId = IdGenerator.generateUUID();
        this.map = new HashMap<>();
    }

    @Override
    public String getExecuteId() {
        return executeId;
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public void put(String key, Object value) {
        map.put(key, value);
    }
}