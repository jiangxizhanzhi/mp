package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentAddRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;

public interface RealEquipmentConverter extends BaseConverter{

    /**
     * 获取实装设备总数
     * @return
     */
    Integer getTotalRealEquipment();

    /**
     * 分页查询
     * @param request
     */
    RealEquipmentConvertModel page(RealEquipmentPageRequest request, String... ignoreFields);

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
    void add(RealEquipmentAddRequest request);

    /**
     * 批量删除
     * @param request
     */
    void delete(RealEquipmentDeleteRequest request);
}
