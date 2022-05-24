package cn.cyberpeace.mp.base.controller.response;

import cn.cyberpeace.mp.base.common.FsCommon;
import lombok.Data;

@Data
public class FsFileUploadResponse extends FileUploadResponse {

    @Override
    public String genHttpUrlFromId(String id) {
        return FsCommon.genHttpUrl(id);
    }

    @Override
    public String genInnerHttpUrlFromId(String id) {
        return FsCommon.genInnerHttpUrl(id);
    }

}
