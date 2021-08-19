package cn.cyberpeace.mp.cscene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import lombok.Data;

@Data
public class BaseCsceneModel extends BaseModel {
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
