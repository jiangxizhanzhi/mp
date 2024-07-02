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
