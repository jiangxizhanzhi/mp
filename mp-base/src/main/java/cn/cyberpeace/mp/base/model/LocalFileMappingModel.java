 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LocalFileMappingModel.java.
 // File size 926.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LocalFileMappingModel
 * @Description 本地文件和远程文件映射模型
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseObjMappingModel
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
