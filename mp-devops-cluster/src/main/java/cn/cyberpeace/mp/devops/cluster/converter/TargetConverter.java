package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;

import java.util.List;

public interface TargetConverter extends BaseConverter{

    /**
     * 查询所有靶标
     * @param request
     * @return
     */
    List<Device> allTarget(TargetSceneRequest request);

}
