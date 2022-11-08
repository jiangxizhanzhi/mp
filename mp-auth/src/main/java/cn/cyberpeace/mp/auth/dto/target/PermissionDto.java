package cn.cyberpeace.mp.auth.dto.target;

import cn.cyberpeace.mp.auth.model.TargetModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class PermissionDto extends TargetModel {

    @ApiModelProperty(value = "R/W/X")
    private String perm;

    @ApiModelProperty(value = "1: 正常 0：禁用")
    private Integer isEnabled;

}
