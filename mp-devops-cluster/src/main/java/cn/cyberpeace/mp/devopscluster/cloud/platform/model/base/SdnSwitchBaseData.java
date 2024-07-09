package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Base64;

/**
 * @ClassName SdnSwitch
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SdnSwitchBaseData extends BaseModel {

    @ApiModelProperty("交换机名称")
    private String name;
    @ApiModelProperty("管理ip")
    private String managerIp;
    @ApiModelProperty("tunnelIp")
    private String tunnelIp;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码，需要base64解码后取出")
    private String password;

    public void base64EncodePw() {
        this.password = new String(Base64.getDecoder().decode(this.password));
    }
}
