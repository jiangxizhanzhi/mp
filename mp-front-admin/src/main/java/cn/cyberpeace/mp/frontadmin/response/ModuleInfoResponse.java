package cn.cyberpeace.mp.frontadmin.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleInfoResponse {

    @ApiModelProperty(value = "模块名")
    private String moduleName;

    @ApiModelProperty(value = "模块是否激活 1:是 0:否")
    private Integer isActived;

}
