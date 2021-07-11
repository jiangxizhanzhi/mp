package cn.cyberpeace.mp.frontadmin.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_front_admin_menu_extra_info")
public class MenuExtraInfoModel extends BaseModel {

    public static Integer PK_DELETE_ALL_START = 0;

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "菜单id")
    private String menu;

    @ApiModelProperty(value = "系统激活keys，多个值用逗号隔开")
    private String activKeys;

    @ApiModelProperty(value = "系统激活版本，多个值用逗号隔开 0:标准版 1:专业版")
    private String editions;

}
