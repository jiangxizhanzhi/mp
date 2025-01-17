 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserCheckService.java.
 // File size 256.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserModel;

 /**
  * 用户检查服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface UserCheckService {

     /**
      * 检查
      *
      * @param userModel 用户模型
      */
     void check(UserModel userModel);

}
