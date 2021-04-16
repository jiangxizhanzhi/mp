package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TenantModel extends BaseModel {
    private String identifier;
    private String name;
}
