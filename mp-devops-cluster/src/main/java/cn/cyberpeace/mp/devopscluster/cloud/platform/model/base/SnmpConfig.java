package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpConfig implements Serializable {

    
    @ApiModelProperty(value = "IP")
    private String ip;

    
    @ApiModelProperty(value = "团体名")
    private String communityName;

    
    @ApiModelProperty(value = "私有KEY")
    private String privKey;

    
    @ApiModelProperty(value = "用户名")
    private String username;

    
    @ApiModelProperty(value = "鉴权KEY")
    private String authKey;
}
