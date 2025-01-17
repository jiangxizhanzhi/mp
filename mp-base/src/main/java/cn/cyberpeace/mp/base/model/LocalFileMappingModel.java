 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocalFileMappingModel.java.
 // File size 788.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 本地文件和远程文件映射模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_base_local_file_mapping")
public class LocalFileMappingModel extends BaseObjMappingModel {
    @DefaultStr
    private String localFile;
    @DefaultStr
    private Long localFileModifiedTime;
    @DefaultStr
    private String remoteFile;

    @Override
    public String retrieveSource() {
        return localFile;
    }

    @Override
    public String retrieveTarget() {
        return remoteFile;
    }
}
