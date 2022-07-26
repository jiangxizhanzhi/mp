package cn.cyberpeace.mp.devops.cluster.repository;

import cn.cyberpeace.mp.devops.cluster.model.EquipmentPositionStatusModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Mapper
@Repository
public interface EquipmentPositionStatusMapper {
    /** 查询所有实装设备位置状态
     * @return
     */
    List<EquipmentPositionStatusModel> selectAll();
}
