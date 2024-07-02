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
