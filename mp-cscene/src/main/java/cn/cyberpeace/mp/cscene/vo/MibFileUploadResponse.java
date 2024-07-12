 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MibFileUploadResponse.java.
 // File size 571.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.vo;

import cn.cyberpeace.mp.cscene.dto.protocolcollection.MibFileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * @BelongsPackage: cn.cyberpeace.mp.cscene.vo
 * @Author: hhuang
 * @CreateTime: 2023/5/18 15:16
 * @Description: mib上传给前端的响应
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MibFileUploadResponse {
    private SceneFileUploadResponse fileInfo;
    private List<MibFileInfo> mib;
}
