package cn.cyberpeace.mp.authbase.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

/**
 * @ClassName BasePermissionModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public abstract class BasePermissionModel extends BaseModel {
    @DefaultStr("default")
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
