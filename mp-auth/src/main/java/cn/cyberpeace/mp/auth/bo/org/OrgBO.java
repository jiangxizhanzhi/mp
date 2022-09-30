package cn.cyberpeace.mp.auth.bo.org;

import cn.cyberpeace.mp.auth.model.OrgModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/9/1 17:20
 */
@Data
public class OrgBO extends OrgModel {

    @ApiModelProperty(value = "路径")
    private String path;

}
