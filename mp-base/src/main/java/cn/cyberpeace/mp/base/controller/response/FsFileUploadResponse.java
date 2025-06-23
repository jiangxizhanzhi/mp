 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FsFileUploadResponse.java.
 // File size 601.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.controller.response;

import cn.cyberpeace.mp.base.common.FsCommon;
import lombok.Data;

/**
 * @ClassName FsFileUploadResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see FileUploadResponse
 */
@Deprecated
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
