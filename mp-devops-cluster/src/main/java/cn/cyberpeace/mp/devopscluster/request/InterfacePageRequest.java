package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.frontadmin.request.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 超融合平台分页请求信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class InterfacePageRequest extends PageRequest {

    @ApiModelProperty(value = "描述")
    private String desc;
}
