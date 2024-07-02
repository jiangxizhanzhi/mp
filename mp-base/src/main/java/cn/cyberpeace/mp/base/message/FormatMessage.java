package cn.cyberpeace.mp.base.message;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class FormatMessage<T> {
    @ApiModelProperty(value = "上下文数据, 目前包含CALL_CHAIN_ID(调用id), LOCALE(LANGUAGE, COUNTRY, VARIANT), PASS_THROUGH(透明上下文)")
    private Map<String, Object> context;
    @ApiModelProperty(value = "数据类型")
    private String type;
    @ApiModelProperty(value = "数据内容")
    private T data;

    /**
     * FormatMessage
     */
    public FormatMessage() {}

    /**
     * FormatMessage
     *
     * @param data
     */
    public FormatMessage(T data) {
        this(data, null);
    }

    /**
     * FormatMessage
     *
     * @param data
     * @param type
     */
    public FormatMessage(T data, String type) {
        this(data, type, null);
    }

    /**
     * FormatMessage
     *
     * @param data
     * @param type
     * @param context
     */
    public FormatMessage(T data, String type, Map<String, Object> context) {
        Map<String, Object> allContext = TransferContext.getContextMap();
        if (context != null) {
            allContext.putAll(context);
        }
        setContext(allContext);
        setData(data);
        setType(type);
    }

}
