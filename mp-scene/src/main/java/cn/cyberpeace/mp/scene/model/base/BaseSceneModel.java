package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseSceneModel extends BaseModel {
    @DefaultStr
    private String source;
    @DefaultStr
    private String name;
    @DefaultStr
    private String description;
    @DefaultStr
    private String file;
    @DefaultStr
    private String displayContext;
    @DefaultBool
    private Boolean deleted;
    @DefaultStr("192.168.1.0/24")
    private String clientLan;
    @DefaultBool
    private String serverVpnIp;

    @Override
    public boolean hasDeleted() {
        return getDeleted();
    }

    @Override
    public boolean prepareDeleteFlag() {
        setDeleted(true);
        return true;
    }
}
