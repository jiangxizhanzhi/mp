 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MessageServiceCode.java.
 // File size 817.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.code;

import cn.cyberpeace.mp.base.common.MessageCommon;
import lombok.Getter;

public class MessageServiceCode implements ServiceCode {

    @Getter
    private final ServiceCode serviceCode;

    @Getter
    private final String[] params;

    public MessageServiceCode(ServiceCode serviceCode, String... params) {
        this.serviceCode = serviceCode;
        this.params = params;
    }

    @Override
    public InnerCode getInnerCode() {
        return serviceCode.getInnerCode();
    }

    @Override
    public String transMessage() {
        if (params == null || params.length == 0) {
            return MessageCommon.get(getMessage());
        } else {
            return MessageCommon.get(getMessage(), params);
        }
    }

}
