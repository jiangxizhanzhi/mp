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
