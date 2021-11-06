package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Description 用于保存业务线程的上下文信息
 * @Author maojun
 * @Date 2021/8/31
 */
@Slf4j
public class SysContextHolder {

    private static final ThreadLocal<SysContext> localSysContextHolder = new ThreadLocal<>();

    private SysContextHolder() {}

    public static void resetSysContext() {
        localSysContextHolder.remove();
    }

    public static SysContext getSysContext() {
        var sysContext = localSysContextHolder.get();
        if (sysContext == null) {
            sysContext = new SysContext();
            sysContext.setCallChainId(TextUtils.uuid());
            localSysContextHolder.set(sysContext);
        }
        return sysContext;
    }

    public static String getCallChainId() {
        return getSysContext().getCallChainId();
    }

    public static void setCallChainId(String callChainId) {
        getSysContext().setCallChainId(callChainId);
    }

    public static void setPassThroughMap(Map<String, Object> map) {
        getSysContext().setPassThroughMap(map);
    }

    public static Map<String, Object> getPassThroughMap() {
        return getSysContext().getPassThroughMap();
    }

}
