package cn.cyberpeace.mp.auth.model.base;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public abstract class BasePermissionModel extends BaseModel {
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;
    private String owner;
    private String target;
    @DefaultBool(true)
    private Boolean canRead;
    @DefaultBool(true)
    private Boolean canCreate;
    @DefaultBool(true)
    private Boolean canUpdate;
    @DefaultBool(true)
    private Boolean canDelete;
}
