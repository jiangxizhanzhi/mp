package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 云镜像管理
 */
@Mapper
@Repository
@ModelClass(DeviceCloudTargetPO.class)
public interface MicrosoftAzureMapper extends BaseModelMapper<DeviceCloudTargetPO> {

    List<DeviceCloudTargetPO> getCloudTargetList(@Param("param") JSONObject requestData);

    Integer deleteByPk(String pk);

    Integer updateCloudTarget(@Param("pk") String pk, @Param("param") JSONObject requestData);

    /**
     * 获取系统码表下一级数据
     * @param parentCode
     * @return
     */
    List<JSONObject> getOS(@Param("parentCode") String parentCode);

    List<DeviceCloudTargetPO> getCloudTargetByPk(@Param("pk") String pk);
}
