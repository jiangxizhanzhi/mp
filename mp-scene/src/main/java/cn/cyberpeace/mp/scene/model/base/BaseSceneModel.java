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
    @DefaultBool
    private Boolean deleted;

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
