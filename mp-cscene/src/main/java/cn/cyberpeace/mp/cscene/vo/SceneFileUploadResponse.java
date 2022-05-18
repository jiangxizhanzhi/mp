package cn.cyberpeace.mp.cscene.vo;

import cn.cyberpeace.mp.base.controller.response.FileUploadResponse;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 */
@Data
public class SceneFileUploadResponse extends FileUploadResponse {
    private String fileName;

    public SceneFileUploadResponse(String id, String fileName) {
        super();
        this.setId(id);
        this.fileName = fileName;
    }
}
