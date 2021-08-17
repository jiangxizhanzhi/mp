package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.ServiceManagePageRequest;

public interface ServiceManageConverter extends BaseConverter{

    /**
     * 分页查询
     * @param request
     */
    RealEquipmentConvertModel page(ServiceManagePageRequest request, String... ignoreFields);

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
    void add(RealEquipmentRequest request);

    /**
     * 批量删除
     * @param request
     */
    void delete(RealEquipmentDeleteRequest request);
}
