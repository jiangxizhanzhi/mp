package cn.cyberpeace.mp.scene.core.bo;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.scene.model.CyberRangeRegisterModel;
import lombok.Data;

@Data
public class MultiSceneRegisterBo implements SnakeHttpData {

    private String rangeTag;

    private String rangeIp;

    private String rangePort;

    public CyberRangeRegisterModel toModel() {
        CyberRangeRegisterModel cyberRangeRegisterModel = new CyberRangeRegisterModel();
        cyberRangeRegisterModel.setRangeTag(this.rangeTag);
        cyberRangeRegisterModel.setRangeIp(this.rangeIp);
        cyberRangeRegisterModel.setRangePort(this.rangePort);
        cyberRangeRegisterModel.setRangeStatus(CyberRangeRegisterModel.RangeStatus.ONLINE);
        return cyberRangeRegisterModel;
    }

    public static MultiSceneRegisterBo fromModel(CyberRangeRegisterModel cyberRangeRegisterModel) {
        MultiSceneRegisterBo multiSceneRegisterBo = new MultiSceneRegisterBo();
        multiSceneRegisterBo.setRangeTag(cyberRangeRegisterModel.getRangeTag());
        multiSceneRegisterBo.setRangeIp(cyberRangeRegisterModel.getRangeIp());
        multiSceneRegisterBo.setRangePort(cyberRangeRegisterModel.getRangePort());
        return multiSceneRegisterBo;
    }
}
