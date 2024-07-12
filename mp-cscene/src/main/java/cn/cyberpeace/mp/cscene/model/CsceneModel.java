 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneModel.java.
 // File size 1228.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
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
 * @ClassName CsceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cscene_cscene")
public class CsceneModel extends BaseCsceneModel {
    @DefaultInt(Type.INSTANCE)
    private Integer type;
    @DefaultInt(1)
    private Integer authType;
    @DefaultStr
    private String logo;
    @DefaultStr
    private String sceneConfig;

    public static class Type {
        public static final int TEMPLATE = 1;
        public static final int INSTANCE = 2;

        private Type() {}
    }

    public static class AuthType {
        private AuthType() {

        }

        public static final int PRIVATE = 0;
        public static final int PUBLIC = 1;
        public static final int SHARED = 2;
    }
}
