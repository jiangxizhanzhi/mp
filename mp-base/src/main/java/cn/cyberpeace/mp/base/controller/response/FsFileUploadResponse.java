package cn.cyberpeace.mp.base.controller.response;

import cn.cyberpeace.mp.base.common.FsCommon;
import lombok.Data;

/**
 * @ClassName FsFileUploadResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see FileUploadResponse
 */
@Deprecated
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
