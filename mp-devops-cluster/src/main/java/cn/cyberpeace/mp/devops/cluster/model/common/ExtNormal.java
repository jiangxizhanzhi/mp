package cn.cyberpeace.mp.devops.cluster.model.common;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtNormal{

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表",hidden = true)
    private List<Resource> resources;
}
