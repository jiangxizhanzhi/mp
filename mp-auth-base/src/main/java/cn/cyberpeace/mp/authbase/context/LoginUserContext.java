 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LoginUserContext.java.
 // File size 665.
 // File create date Fri Jan 17 09:57:14 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.authbase.context;

import cn.cyberpeace.mp.base.context.transfercontext.TransferContext;
import org.springframework.stereotype.Component;

 /**
  * 登录用户背景信息
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class LoginUserContext implements TransferContext<String> {

     /**
      *
      */
     private static String KEY = "USER_ID";

     /**
      * 得到关键
      *
      * @return {@link String }
      */
     @Override
    public String getKey() {
        return KEY;
    }

     /**
      * 获得价值
      *
      * @return {@link String }
      */
     @Override
    public String getValue() {
        return LoginUserHolder.get().get();
    }

     /**
      * 设置值
      *
      * @param value 价值
      */
     @Override
    public void setValue(String value) {
        LoginUserHolder.get().set(value);
    }
}
