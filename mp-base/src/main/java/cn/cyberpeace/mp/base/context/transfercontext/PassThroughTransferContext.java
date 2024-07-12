 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PassThroughTransferContext.java.
 // File size 970.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.context.transfercontext;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName PassThroughTransferContext
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see TransferContext
 */
@Component
public class PassThroughTransferContext implements TransferContext<Map<String, Object>> {

    /**
     * getKey
     *
     * @return
     */
    @Override
    public String getKey() {
        return "PASS_THROUGH";
    }

    /**
     * getValue
     *
     * @return
     */
    @Override
    public Map<String, Object> getValue() {
        return SysContextHolder.getPassThroughMap();
    }

    /**
     * setValue
     *
     * @param value
     */
    @Override
    public void setValue(Map<String, Object> value) {
        SysContextHolder.setPassThroughMap(value);
    }
}
