package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ModifySdnSwitchCommand {
    @ApiModelProperty("管理ip")
    @NotBlank
    private String managerIp;
    @ApiModelProperty("tunnelIp")
    @NotBlank
    private String tunnelIp;
    @ApiModelProperty("用户名")
    @NotBlank
    private String username;
    @ApiModelProperty("密码，需要base64 encode后存入")
    @NotBlank
    private String password;


    public void base64EncodePw() {
        this.password = Base64.getEncoder().encodeToString(this.password.getBytes(StandardCharsets.UTF_8));
    }
}
