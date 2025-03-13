package cn.cyberpeace.mp.auth.bo.sso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.bo.sso
 * @Author: hhuang
 * @CreateTime: 2025/03/13  16:11
 * @Description: sso登录业务对象
 * @Version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SSOLoginBO {

    /**
     * 票据
     */
    private String ticket;

    /**
     * 跳转地址
     */
    private String service;

    /**
     * 请求
     */
    private HttpServletRequest request;

    /**
     * 响应
     */
    private HttpServletResponse response;
}
