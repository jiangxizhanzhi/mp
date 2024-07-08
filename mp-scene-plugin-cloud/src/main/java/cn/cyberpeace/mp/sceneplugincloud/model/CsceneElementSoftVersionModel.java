package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("mp_cscene_element_soft_version")
/**
 * CsceneElementSoftVersionModel 类继承自 BaseModel，用于表示场景元素的软件版本信息。
 * 该类包含了场景元素的唯一标识、软件名称和版本号等基本信息。
 */
public class CsceneElementSoftVersionModel extends BaseModel {

    /**
     * 场景元素的唯一标识符。
     * 使用 @DefaultStr 注解指定其默认值，确保该字段在使用前已被初始化。
     */
    @DefaultStr
    private String eid;

    /**
     * 软件的名称。
     * 使用 @DefaultStr 注解指定其默认值，确保该字段在使用前已被初始化。
     */
    @DefaultStr
    private String soft;

    /**
     * 软件的版本号。
     * 使用 @DefaultStr 注解指定其默认值，确保该字段在使用前已被初始化。
     */
    @DefaultStr
    private String version;
}

