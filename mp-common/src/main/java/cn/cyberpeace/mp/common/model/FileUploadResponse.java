package cn.cyberpeace.mp.common.model;

import cn.cyberpeace.mp.base.controller.response.FsFileUploadResponse;
import lombok.Data;

@Data
public class FileUploadResponse extends FsFileUploadResponse {
    private String fileName;

    public FileUploadResponse(String id, String fileName) {
        super();
        this.setId(id);
        this.fileName = fileName;
    }
}
