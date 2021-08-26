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
    @DefaultInt(2)
    private Integer type;
    @DefaultStr
    private String scene;

    public static class Type {
        public static final Integer TEST = 1;
        public static final Integer APP = 2;

        private Type() {}
    }
}
