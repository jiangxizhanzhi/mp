package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.util.IdGenerator;

/**
 * @Description
 * @Author maojun
 * @Date 2021/8/31
 */
public class BaseSysContext implements SysContext {
    private String executeId;

    public BaseSysContext() {
        this.executeId = IdGenerator.generateUUID();
    }

    @Override
    public String getExecuteId() {
        return executeId;
    }
}