package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
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
    @DefaultInt
    private Integer cloudType ;
    @DefaultStr
    private String intervalSec;

    public static class CloudType {
        public static final int DEFAULT = 0;
        public static final int PUBLIC = 1;
        public static final int PRIVATE = 2;
        public static final int MIX = 3;

        private CloudType() {
        }
    }

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
