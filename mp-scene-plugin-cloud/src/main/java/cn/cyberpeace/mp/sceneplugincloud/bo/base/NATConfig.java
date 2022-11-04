package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 
 * @author zhangjiaxin
 * @date 2022年11月4日下午2:14:23 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NATConfig {

    @ApiModelProperty(value = "外网地址")
    private String wanAddress;
    
    @ApiModelProperty(value = "内网地址")
    private String lanAddress;
    
    @ApiModelProperty(value = "端口号")
    private Integer port;
    
}
