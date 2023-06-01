package cn.cyberpeace.mp.frontadmin.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/05/31 16:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_sys_deploy_log")
public class SysDeployLogModel extends BaseModel {

    @ApiModelProperty(value = "系统名称")
    private String system;

    @ApiModelProperty(value = "版本")
    private String version;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "状态，0:失败 1:成功")
    private Integer status;
}
