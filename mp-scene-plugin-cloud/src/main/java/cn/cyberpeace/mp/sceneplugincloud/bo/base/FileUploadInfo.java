 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FileUploadInfo.java.
 // File size 506.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName FileUploadInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
public class FileUploadInfo implements Serializable {

    private static final long serialVersionUID = -5413933571955145789L;

    private String id;
    private String httpUrl;
    private String innerHttpUrl;
    private String fileName;
}
