package cn.cyberpeace.mp.base.message;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import lombok.Data;

import java.util.Map;

@Data
public class FormatMessage<T> {
    private Map<String, Object> context;
    private String type;
    private T data;

    public FormatMessage() {}

    public FormatMessage(T data) {
        this(data, null);
    }

    public FormatMessage(T data, String type) {
        this(data, type, null);
    }

    public FormatMessage(T data, String type, Map<String, Object> context) {
        var allContext = TransferContext.getContextMap();
        if (context != null) {
            allContext.putAll(context);
        }
        setContext(allContext);
        setData(data);
        setType(type);
    }

}
