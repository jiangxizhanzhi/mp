package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cscene_cscene")
public class CsceneModel extends BaseCsceneModel {
    @DefaultInt(2)
    private Integer type;
    @DefaultStr
    private String sceneConfig;

    public static class Type {
        public static final Integer TEMPLATE = 1;
        public static final Integer INSTANCE = 2;

        private Type() {}
    }
}
