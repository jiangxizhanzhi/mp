package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeBO {

    private String rangeName;

    private String rangeIp;
}
