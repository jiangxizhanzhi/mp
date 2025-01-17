 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FsFileUploadResponse.java.
 // File size 438.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.controller.response;

import cn.cyberpeace.mp.base.common.FsCommon;
import lombok.Data;

@Data
public class FsFileUploadResponse extends FileUploadResponse {

    @Override
    public String genHttpUrlFromId(String id) {
        return FsCommon.genHttpUrl(id);
    }

    @Override
    public String genInnerHttpUrlFromId(String id) {
        return FsCommon.genInnerHttpUrl(id);
    }

}
