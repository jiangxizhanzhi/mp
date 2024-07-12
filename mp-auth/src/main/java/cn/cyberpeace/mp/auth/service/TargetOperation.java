 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetOperation.java.
 // File size 387.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;

/**
 * @ClassName TargetOperation
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
public interface TargetOperation<T> {

    /**
     * doExecute
     *
     * @param targetBo
     * @return
     */
    T doExecute(TargetBo targetBo);
}
