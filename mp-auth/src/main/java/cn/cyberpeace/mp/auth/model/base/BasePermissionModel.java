package cn.cyberpeace.mp.auth.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public abstract class BasePermissionModel extends BaseModel {
    private String tenant;
    private String owner;
    private String target;
    private Boolean canRead;
    private Boolean canCreate;
    private Boolean canUpdate;
    private Boolean canDelete;
}
