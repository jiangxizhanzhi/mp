 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceModel.java.
 // File size 1197.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CsceneInstanceModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cscene_cscene_instance")
public class CsceneInstanceModel extends BaseCsceneModel {
    @DefaultStr
    private String cscene;
    @DefaultStr
    private String logo;
    @DefaultInt(Type.APP)
    private Integer type;
    @DefaultStr
    private String scene;
    @DefaultStr
    private String createUser;
    @DefaultInt
    private Integer sceneTtl;
    @DefaultInt
    private Integer sceneAttr;
    @DefaultStr
    private String passThrough;

    public static class Type {
        public static final int TEST = 1;
        public static final int APP = 2;

        private Type() {}
    }
}
