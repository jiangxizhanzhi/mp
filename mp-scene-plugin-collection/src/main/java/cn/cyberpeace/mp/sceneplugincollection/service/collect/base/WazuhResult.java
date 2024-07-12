 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhResult.java.
 // File size 644.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName WazuhResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Getter
@Setter
public class WazuhResult<T> {
    private List<T> list;
    private long total;

    /**
     * WazuhResult
     */
    public WazuhResult() {

    }

    /**
     * WazuhResult
     *
     * @param list
     * @param total
     */
    public WazuhResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }
}
