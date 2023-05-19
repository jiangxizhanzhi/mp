package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.bo.base
 * @Author: hhuang
 * @CreateTime: 2023/05/18  17:44
 * @Description: 上传文件信息
 * @Version: 1.0
 */
@Data
public class FileUploadInfo implements Serializable {

    private static final long serialVersionUID = -5413933571955145789L;

    private String id;
    private String httpUrl;
    private String innerHttpUrl;
    private String fileName;
}
