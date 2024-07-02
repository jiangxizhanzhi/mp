package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName ExtNormal
 * @Description 扩展服务信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtNormal implements Serializable {

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表",hidden = true)
    private String resources;
}
