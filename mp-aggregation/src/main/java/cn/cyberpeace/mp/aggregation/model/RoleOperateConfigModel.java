package cn.cyberpeace.mp.aggregation.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("mp_role_operate_config")
public class RoleOperateConfigModel extends BaseModel {
    @DefaultStr
    private String module;
    @DefaultStr
    private String key;
    @DefaultStr
    private String url;
    @DefaultStr
    private String desc;
}
