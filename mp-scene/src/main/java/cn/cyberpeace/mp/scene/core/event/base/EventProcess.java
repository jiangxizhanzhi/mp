package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.util.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventProcess {
    private String process;
    private String processName;

    public String getProcessNameText() {
        return MessageUtils.get(processName);
    }
}
