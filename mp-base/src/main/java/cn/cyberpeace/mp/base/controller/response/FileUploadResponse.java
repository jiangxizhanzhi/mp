 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileUploadResponse.java.
 // File size 673.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.controller.response;

import lombok.Data;

/**
 * @ClassName FileUploadResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Deprecated
@Data
public class FileUploadResponse {
    private String id;
    private String httpUrl;
    private String innerHttpUrl;

    public void setId(String id) {
        this.id = id;
        setHttpUrl(genHttpUrlFromId(id));
        setInnerHttpUrl(genInnerHttpUrlFromId(id));
    }

    public String genHttpUrlFromId(String id) {
        return null;
    }

    public String genInnerHttpUrlFromId(String id) {
        return null;
    }

}
