 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgDtoOperation.java.
 // File size 247.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.org.OrgDto;

 /**
  * 组织运作
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface OrgDtoOperation<T> {

     /**
      * 做执行
      *
      * @param orgDto org dto
      * @return {@link T }
      */
     T doExecute(OrgDto orgDto);
}
