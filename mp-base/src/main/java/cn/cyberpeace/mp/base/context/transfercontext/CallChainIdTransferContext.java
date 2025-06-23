 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CallChainIdTransferContext.java.
 // File size 899.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.stereotype.Component;

/**
 * @ClassName CallChainIdTransferContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TransferContext
 */
@Component
public class CallChainIdTransferContext implements TransferContext<String> {
    /**
     * getKey
     *
     * @return
     */
    @Override
    public String getKey() {
        return "CALL_CHAIN_ID";
    }

    /**
     * getValue
     *
     * @return
     */
    @Override
    public String getValue() {
        return SysContextHolder.getCallChainId();
    }

    /**
     * setValue
     *
     * @param value
     */
    @Override
    public void setValue(String value) {
        SysContextHolder.setCallChainId(value);
    }
}
