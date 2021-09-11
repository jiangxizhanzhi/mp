package cn.cyberpeace.mp.cscene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseCsceneRoleModel extends BaseModel {
    @DefaultStr
    private String cscene;
    @DefaultStr
    private String parent;
    @DefaultStr
    private String key;
    @DefaultStr
    private String name;
    @DefaultStr
    private String type;
    @DefaultStr
    private String assetNodes;
    @DefaultStr
    private String visibleNodes;
    @DefaultStr
    private String users;
}
