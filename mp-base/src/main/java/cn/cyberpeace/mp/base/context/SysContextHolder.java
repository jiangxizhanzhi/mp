package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @ClassName SysContextHolder
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class SysContextHolder {

    private static final ThreadLocal<SysContext> localSysContextHolder = new ThreadLocal<>();

    /**
     * SysContextHolder
     */
    private SysContextHolder() {}

    /**
     * resetSysContext
     */
    public static void resetSysContext() {
        localSysContextHolder.remove();
    }

    /**
     * getSysContext
     *
     * @return
     */
    public static SysContext getSysContext() {
        SysContext sysContext = localSysContextHolder.get();
        if (sysContext == null) {
            sysContext = new SysContext();
            sysContext.setCallChainId(TextUtils.uuid());
            localSysContextHolder.set(sysContext);
        }
        return sysContext;
    }

    /**
     * getCallChainId
     *
     * @return
     */
    public static String getCallChainId() {
        return getSysContext().getCallChainId();
    }

    /**
     * setCallChainId
     *
     * @param callChainId
     */
    public static void setCallChainId(String callChainId) {
        getSysContext().setCallChainId(callChainId);
    }

    /**
     * setPassThroughMap
     *
     * @param map
     */
    public static void setPassThroughMap(Map<String, Object> map) {
        getSysContext().setPassThroughMap(map);
    }

    /**
     * getPassThroughMap
     *
     * @return
     */
    public static Map<String, Object> getPassThroughMap() {
        return getSysContext().getPassThroughMap();
    }

}
