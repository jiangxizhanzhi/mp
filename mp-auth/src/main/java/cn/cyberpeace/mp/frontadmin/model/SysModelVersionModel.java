package cn.cyberpeace.mp.frontadmin.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: guquan
 * @Date：2023/8/23 15:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sys_model_version")
public class SysModelVersionModel extends BaseModel {

    @ApiModelProperty(value = "模块名称")
    private String modelName;

    @ApiModelProperty(value = "模块版本号")
    private String modelVersion;

    @ApiModelProperty(value = "是否可见")
    @DefaultBool
    private Boolean visible;
}
