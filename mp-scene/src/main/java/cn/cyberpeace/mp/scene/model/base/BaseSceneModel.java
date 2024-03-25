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
    @DefaultInt(MultiSceneType.COMMON_SCENE)
    private Integer multiSceneType;
    @DefaultStr
    private String sceneCreateRangeTag;



    @Override
    public boolean hasDeleted() {
        return getDeleted();
    }

    @Override
    public boolean prepareDeleteFlag() {
        setDeleted(true);
        return true;
    }

    public static class MultiSceneType {
        public static final int COMMON_SCENE = 0;

        public static final int MULTI_SCENE = 1;
    }
}
