 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ServiceCode.java.
 // File size 741.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.code;

import cn.cyberpeace.mp.base.common.MessageCommon;

public interface ServiceCode {

    default void setCode(String code, String message) {
        var innerCode = getInnerCode();
        innerCode.setCode(code);
        innerCode.setMessage(message);
    }

    default String getCode() {
        return getInnerCode().getCode();
    }

    default String getMessage() {
        return getInnerCode().getMessage();
    }

    InnerCode getInnerCode();

    default String transMessage() {
        return MessageCommon.get(getMessage());
    }

    default MessageServiceCode format(String... params) {
        return new MessageServiceCode(this, params);
    }

}
