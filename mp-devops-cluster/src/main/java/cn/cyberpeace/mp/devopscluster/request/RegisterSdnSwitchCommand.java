package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class RegisterSdnSwitchCommand {
    @ApiModelProperty("交换机名称")
    private String name;
    @ApiModelProperty("管理ip")
    private String managerIp;
    @ApiModelProperty("tunnelIp")
    private String tunnelIp;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码，需要base64 encode后存入")
    private String password;


    public void base64EncodePw() {
        this.password = Base64.getEncoder().encodeToString(this.password.getBytes(StandardCharsets.UTF_8));
    }
}
