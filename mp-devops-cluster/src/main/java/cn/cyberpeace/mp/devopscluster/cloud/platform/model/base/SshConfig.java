package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SshConfig implements Serializable {

    @ApiModelProperty(value = "host")
    private String ip;
    @ApiModelProperty(value = "port")
    private Integer port;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("文件名")
    private String file;
}
