 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivationVerifyInfo.java.
 // File size 472.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName ActivationVerifyInfo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
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
