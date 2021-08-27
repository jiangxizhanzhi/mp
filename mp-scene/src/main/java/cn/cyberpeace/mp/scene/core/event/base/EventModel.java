package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventModel extends BaseModel {
    @DefaultStr
    private String target;
    @DefaultInt
    private Integer event;
    @DefaultInt
    private Integer eventStatus;
    @DefaultInt
    private Integer process;
    @DefaultInt
    private Integer processStatus;
    @DefaultStr
    private String processMessage;
}
