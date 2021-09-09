package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.SwitchConvertModel;

import java.util.List;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
public interface SwitchConverter {

    /**
     * 查询交换机信息
     * @return
     */
    List<SwitchConvertModel> list();
}
