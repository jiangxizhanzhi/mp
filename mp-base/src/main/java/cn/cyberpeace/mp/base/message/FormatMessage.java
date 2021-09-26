package cn.cyberpeace.mp.base.message;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import cn.cyberpeace.mp.base.util.JsonUtils;
import lombok.Data;

import java.util.Map;

@Data
public class FormatMessage {
    private Map<String, Object> context;
    private String type;
    private String content;

    public static FormatMessage fromMessage(String message) {
        var formatMessage = new FormatMessage();
        formatMessage.setContext(TransferContext.getContextMap());
        formatMessage.setContent(message);
        return formatMessage;
    }

    public String toMessageString() {
        return JsonUtils.toJsonString(this);
    }
}
