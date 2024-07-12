 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HandlerUtils.java.
 // File size 646.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName HandlerUtils
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Getter
public class HandlerUtils {

    /**
     * genResourceName
     *
     * @param name
     * @return
     */
    public static String genResourceName(String name) {
        return StringUtils.joinWith("_",
                cn.cyberpeace.mp.sceneplugincloud.plugin.common.HandlerUtils.genResourceName(name), "collection");
    }

}
