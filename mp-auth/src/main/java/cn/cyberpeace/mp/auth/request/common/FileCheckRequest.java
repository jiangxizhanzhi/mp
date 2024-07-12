 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileCheckRequest.java.
 // File size 446.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.request.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName FileCheckRequest
 * @Description 封装文件验证参数
 * @Author shgang
 * @Date 2022/10/27
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class FileCheckRequest {
    private Long maxSize;
    private List<String> suffixList;
    private List<String> magicHeaderList;
}
