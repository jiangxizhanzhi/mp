package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("mp_cscene_element_soft_version")
public class CsceneElementSoftVersionModel extends BaseModel {

    @DefaultStr
    private String eid;

    @DefaultStr
    private String soft;

    @DefaultStr
    private String version;
}
