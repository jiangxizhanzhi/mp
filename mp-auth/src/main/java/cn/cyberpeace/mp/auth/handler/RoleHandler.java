package cn.cyberpeace.mp.auth.handler;

import javax.servlet.http.HttpServletRequest;

/*
 * @BelongsPackage: cn.cyberpeace.mp.auth.handler
 * @Author: hhuang
 * @CreateTime: 2023/3/25 11:49
 * @Description: 角色处理接口
 * @Version: 1.0
 */
public interface RoleHandler {
    /**
     * 核心处理
     */
    void handle(HttpServletRequest request) throws Exception;
}
