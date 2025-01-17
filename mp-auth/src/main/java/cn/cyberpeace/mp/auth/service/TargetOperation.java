 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetOperation.java.
 // File size 256.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface TargetOperation<T> {

    T doExecute(TargetBo targetBo);
}
