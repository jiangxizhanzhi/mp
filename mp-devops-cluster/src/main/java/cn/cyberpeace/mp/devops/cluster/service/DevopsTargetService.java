package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetPO;

import java.util.List;
import java.util.Map;

/**
 * 靶标管理
 * @author wanggang
 */
public interface DevopsTargetService {

    /**
     * 分页查询
     * @param dto
     * @return
     */
    List<DeviceTargetPO> page(ImagePageDTO dto);

    /**
     * 获取总数
     * @param dto
     * @return
     */
    Integer totalCount(ImagePageDTO dto);

    /**
     * 更新
     * @param device
     * @return
     */
    Integer updateById(Device device);

    /**
     * 新增
     * @param deviceList
     * @return
     */
    Integer insert(List<Device> deviceList);

    /**
     * 靶标类型统计
     * @return
     */
    Map<String,Object> countTarget();

    /**
     * 虚拟靶标统计
     * @return
     */
    Map<String,Object> countVmTarget();

    /**
     * 实体靶标统计
     * @return
     */
    Map<String,Object> countEntityTarget();

    /**
     * 删除所有靶标数据
     * @return
     */
    int deleteAllTarget();

    /**
     * 删除
     * @param dto
     * @return
     */
    Integer delete(ImageDeleteDTO dto);
}
