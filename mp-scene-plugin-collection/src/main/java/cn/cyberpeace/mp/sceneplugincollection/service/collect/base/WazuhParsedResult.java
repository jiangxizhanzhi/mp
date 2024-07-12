 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhParsedResult.java.
 // File size 375.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.service.collect.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName WazuhParsedResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Setter
@Getter
public class WazuhParsedResult<T> {
    private List<T> data;
    private long total;
}
