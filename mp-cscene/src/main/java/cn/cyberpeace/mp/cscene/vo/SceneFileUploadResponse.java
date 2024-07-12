 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneFileUploadResponse.java.
 // File size 550.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.vo;

import cn.cyberpeace.mp.base.controller.response.FsFileUploadResponse;
import lombok.Data;

/**
 * @ClassName SceneFileUploadResponse
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 * @Version 1.0.0
 * @see FsFileUploadResponse
 */
@Data
public class SceneFileUploadResponse extends FsFileUploadResponse {
    private String fileName;

    public SceneFileUploadResponse(String id, String fileName) {
        super();
        this.setId(id);
        this.fileName = fileName;
    }
}
