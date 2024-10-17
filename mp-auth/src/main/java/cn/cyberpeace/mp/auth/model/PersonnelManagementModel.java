package cn.cyberpeace.mp.auth.model;


import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_personnel_management")
@Builder
public class PersonnelManagementModel extends BaseModel {
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "战队id")
    private String teamId;
}
