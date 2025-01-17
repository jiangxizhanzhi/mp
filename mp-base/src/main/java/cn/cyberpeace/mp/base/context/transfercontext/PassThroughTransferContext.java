 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PassThroughTransferContext.java.
 // File size 637.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PassThroughTransferContext implements TransferContext<Map<String, Object>> {

    @Override
    public String getKey() {
        return "PASS_THROUGH";
    }

    @Override
    public Map<String, Object> getValue() {
        return SysContextHolder.getPassThroughMap();
    }

    @Override
    public void setValue(Map<String, Object> value) {
        SysContextHolder.setPassThroughMap(value);
    }
}
