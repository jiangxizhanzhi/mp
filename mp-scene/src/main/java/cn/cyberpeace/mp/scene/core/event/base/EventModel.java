package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventModel extends BaseModel {
    @DefaultStr
    private String target;
    @DefaultInt
    private Integer event;
    @DefaultInt
    private Integer eventStatus;
    @DefaultInt
    private String process;
    @DefaultInt
    private Integer processStatus;
    @DefaultStr
    private String processMessage;
}
