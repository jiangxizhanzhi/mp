package cn.cyberpeace.mp.cscene.vo;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.LfsCommon;
import cn.cyberpeace.mp.base.controller.response.FsFileUploadResponse;
import lombok.Data;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/18
 */
@Data
public class SceneFileUploadResponse extends FsFileUploadResponse {
    private String fileName;

    public SceneFileUploadResponse(String id, String fileName) {
        super();
        this.setId(id);
        this.fileName = fileName;
    }

    @Override
    public String genHttpUrlFromId(String id) {
        return LfsCommon.genHttpUrl(id);
    }

    @Override
    public String genInnerHttpUrlFromId(String id) {
        return LfsCommon.genInnerHttpUrl(id);
    }
}
