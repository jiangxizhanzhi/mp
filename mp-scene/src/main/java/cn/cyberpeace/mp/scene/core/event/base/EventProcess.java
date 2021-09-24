package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.util.MessageUtils;
import lombok.Data;

@Data
public class EventProcess {
    private String process;
    private String processMessage;

    public String getProcessMessageText() {
        return MessageUtils.get(processMessage);
    }
}
