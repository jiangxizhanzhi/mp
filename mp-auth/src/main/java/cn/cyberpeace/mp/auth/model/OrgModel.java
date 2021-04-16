package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrgModel extends BaseModel {
    private String tenant;
    private String parent;
    private String identifier;
    private String name;
}
