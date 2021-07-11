package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
@ModelClass(DeviceTargetPO.class)
public interface DeviceTargetMapper extends BaseModelMapper<DeviceTargetPO> {

    
    List<DeviceTargetPO> page(ImagePageDTO dto);

    
    Integer totalCount(ImagePageDTO dto);

    
    DeviceTargetPO findByUuid(String uuid);

    
    List<DeviceTargetPO> findByUuids(@Param("uuids") List<String> uuids);

    
    List<DeviceTargetPO> findByIds(@Param("ids") List<String> ids);

    
    Integer updateById(DeviceTargetPO deviceTargetPO);

    
    Integer delete(ImageDeleteDTO dto);

    
    Integer updateBatch(@Param("list") List<DeviceTargetPO> list);

    Integer batchUpdateInPosition(@Param("list") List<DeviceTargetPO> list);

    
    Integer batchInsert(@Param("list") List<DeviceTargetPO> list);

    
    Map<String,Object> countTarget();

    
    Map<String,Object> countVmTarget();

    
    Map<String,Object> countEntityTarget();

    
    Integer deleteAllTarget();
}
