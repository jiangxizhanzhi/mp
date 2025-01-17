 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LoginUserHolder.java.
 // File size 327.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.authbase.context;

 /**
  * @author 二竹
  * @date 2025/01/17
  */
 public class LoginUserHolder {

     /**
      * 用户上下文
      */
     private static ThreadLocal<String> userContext = new ThreadLocal();

     /**
      * 得到
      *
      * @return {@link ThreadLocal }<{@link String }>
      */
     public static ThreadLocal<String> get() {
        return userContext;
    }


}
