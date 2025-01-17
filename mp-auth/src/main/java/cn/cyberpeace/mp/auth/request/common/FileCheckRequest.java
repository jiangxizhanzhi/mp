 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileCheckRequest.java.
 // File size 395.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.request.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description 封装文件验证参数
 * @Author shgang
 * @Date 2022/10/27
 */
@Data
@NoArgsConstructor
public class FileCheckRequest {
    private Long maxSize;
    private List<String> suffixList;
    private List<String> magicHeaderList;
}
