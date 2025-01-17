 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileUploadResponse.java.
 // File size 540.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.controller.response;

import lombok.Data;

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
