package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public abstract class BaseObjMappingModel extends BaseModel {
    @DefaultStr
    private String group;
    @DefaultInt
    private Integer seq;
    @DefaultGen(UuidGenerator.class)
    private String name;

    public abstract String getSource();

    public abstract String getTarget();
}
