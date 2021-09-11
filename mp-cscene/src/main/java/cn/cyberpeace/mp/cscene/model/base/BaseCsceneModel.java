package cn.cyberpeace.mp.cscene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseCsceneModel extends BaseModel {
    @DefaultInt
    private Integer referenceType;
    @DefaultStr
    private String createUser;
    @DefaultStr
    private String createUsername;
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

    public static class ReferenceType {

        private ReferenceType() {}
    }
}
