 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ControllerConstant.java.
 // File size 528.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.constant;

/**
 * @ClassName ControllerConstant
 * @Description 控制层常量池
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public final class ControllerConstant {

    /**
     * ControllerConstant
     */
    private ControllerConstant(){
        throw new IllegalStateException("Utility class");
    }

    public static final String BASE_API_PATH = "/devops/cluster";

    public static final String ERROR_CODE_PREFIX = "devops.error_code.";
}
