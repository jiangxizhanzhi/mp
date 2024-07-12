 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LoginUserHolder.java.
 // File size 428.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.authbase.context;

/**
 * @ClassName LoginUserHolder
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/10/13 18:49
 * @Version 1.0.0
 */
public class LoginUserHolder {

    private static ThreadLocal<String> userContext = new ThreadLocal();

    /**
     * get
     *
     * @return
     */
    public static ThreadLocal<String> get() {
        return userContext;
    }


}
