package cn.cyberpeace.mp.cscene.vo;

import cn.cyberpeace.mp.base.controller.response.FsFileUploadResponse;
import lombok.Data;


@Data
public class SceneFileUploadResponse extends FsFileUploadResponse {
    private String fileName;

    public SceneFileUploadResponse(String id, String fileName) {
        super();
        this.setId(id);
        this.fileName = fileName;
    }
}
