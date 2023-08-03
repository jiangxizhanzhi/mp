package cn.cyberpeace.mp.cscene.controller.request;

import lombok.Data;

@Data
public class CsceneInstanceNodeUploadFileActionRequest extends CsceneInstanceNodeActionRequest {
    private String fileName;
    private String fileUrl;
}
