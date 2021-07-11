package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import lombok.Data;

import java.io.Serializable;


@Data
public class FileUploadInfo implements Serializable {

    private static final long serialVersionUID = -5413933571955145789L;

    private String id;
    private String httpUrl;
    private String innerHttpUrl;
    private String fileName;
}
