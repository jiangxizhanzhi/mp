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
@Table("mp_cscene_cscene_instance")
public class CsceneInstanceModel extends BaseCsceneModel {
    @DefaultStr
    private String cscene;
    @DefaultInt(Type.APP)
    private Integer type;
    @DefaultStr
    private String scene;

    public static class Type {
        public static final int TEST = 1;
        public static final int APP = 2;

        private Type() {}
    }
}
