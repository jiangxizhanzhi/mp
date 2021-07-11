package cn.cyberpeace.mp.auth.bo.org;

import cn.cyberpeace.mp.auth.model.OrgModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class OrgBO extends OrgModel {

    @ApiModelProperty(value = "路径")
    private String path;

}
