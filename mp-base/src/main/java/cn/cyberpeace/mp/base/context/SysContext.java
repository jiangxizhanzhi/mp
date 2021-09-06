package cn.cyberpeace.mp.base.context;

/**
 * @Description 系统请求的上下文环境变量，用于存储通用的上下文信息
 * @Author maojun
 * @Date 2021/8/31
 */
public interface SysContext {
    /**
     * 本次处理过程的唯一Id
     * @return
     */
    String getExecuteId();
}
