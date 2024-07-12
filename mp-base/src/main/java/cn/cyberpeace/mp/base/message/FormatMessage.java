 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FormatMessage.java.
 // File size 1537.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.message;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName FormatMessage
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
