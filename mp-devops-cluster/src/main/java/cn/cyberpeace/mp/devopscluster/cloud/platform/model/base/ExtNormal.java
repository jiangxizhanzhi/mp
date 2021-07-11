package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtNormal implements Serializable {

    
    @ApiModelProperty(value = "资源列表",hidden = true)
    private String resources;
}
