package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.request.TargetPageRequest;

import java.util.List;

public interface RealEquipmentConverter extends BaseConverter{

    /**
     * 获取实装设备总数
     * @return
     */
    Integer getTotalRealEquipment(RealEquipmentPageRequest request);

    /**
     * 分页查询
     * @param request
     */
    RealEquipmentConvertModel page(RealEquipmentPageRequest request);

    /**
     * 查询所有靶标
     * @param request
     * @return
     */
    List<Device> allTarget(TargetPageRequest request);

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    RealEquipment findById(String id);

    /**
     * 新增
     * @param request
     */
    RealEquipment add(RealEquipmentRequest request);

    /**
     * 更新
     * @param request
     */
    RealEquipment update(RealEquipmentRequest request);

    /**
     * 批量删除
     * @param request
     */
    Boolean delete(RealEquipmentDeleteRequest request);
}
