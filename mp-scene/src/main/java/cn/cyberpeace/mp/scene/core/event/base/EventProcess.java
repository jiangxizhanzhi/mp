package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.common.MessageCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventProcess {
    private String process;
    private String processName;

    //获取下一个过程名称
    public String getProcessNameText() {
        return MessageCommon.get(processName);
    }
}
