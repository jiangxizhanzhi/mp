package cn.cyberpeace.mp.auth.request.sso;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.request.sso
 * @Author: hhuang
 * @CreateTime: 2025/03/13  17:45
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class SSOLoginRequest {
    /**
     * 票据
     */
    @NotEmpty
    private String ticket;

    /**
     * 跳转地址
     */
    private String service;
}
