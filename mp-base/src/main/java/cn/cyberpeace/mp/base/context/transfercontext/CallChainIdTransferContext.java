 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CallChainIdTransferContext.java.
 // File size 566.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CallChainIdTransferContext implements TransferContext<String> {
    @Override
    public String getKey() {
        return "CALL_CHAIN_ID";
    }

    @Override
    public String getValue() {
        return SysContextHolder.getCallChainId();
    }

    @Override
    public void setValue(String value) {
        SysContextHolder.setCallChainId(value);
    }
}
