package cn.cyberpeace.mp.base.controller.response;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import lombok.Data;

@Data
public class FdfsFileUploadResponse extends FileUploadResponse {

    @Override
    public String genHttpUrlFromId(String id) {
        return FdfsCommon.genHttpUrl(id);
    }

    @Override
    public String genInnerHttpUrlFromId(String id) {
        return FdfsCommon.genInnerHttpUrl(id);
    }

}
