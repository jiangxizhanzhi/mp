 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationVerifyInfo.java.
 // File size 420.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import lombok.Data;

import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationVerifyInfo {

    // 机器码
    private String machinecode;

    // 授权月份（多少个月）
    private Integer deadline;

    // 其他信息
    private Map<String, String> info;

}
