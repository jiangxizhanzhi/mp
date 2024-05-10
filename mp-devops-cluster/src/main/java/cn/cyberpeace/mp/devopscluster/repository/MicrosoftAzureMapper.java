package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.bo.MpSysCodeEnum;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetCalculate;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetContainer;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetPO;
import cn.cyberpeace.mp.devopscluster.po.DeviceCloudTargetVO;
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

    List<DeviceCloudTargetCalculate> getCloudTargetCalculateList(@Param("param") JSONObject requestData);

    List<DeviceCloudTargetContainer> getCloudTargetContainerList(@Param("param") JSONObject requestData);

    Integer getCloudTargetListTotal(@Param("param") JSONObject requestData);

    Integer deleteByIds(@Param("id") String id, @Param("param") List<String> ids);

    Integer deleteCalculatesByIds(@Param("id") String id, @Param("param") List<String> ids);

    Integer deleteContainersByIds(@Param("id") String id, @Param("param") List<String> ids);

    Integer updateCloudCalculateTarget(@Param("id") String id, @Param("param") JSONObject requestData);

    Integer updateCloudContainerTarget(@Param("id") String id, @Param("param") JSONObject requestData);

    DeviceCloudTargetVO getCloudTargetCalculateById(@Param("id") String id);

    DeviceCloudTargetVO getCloudTargetContainerById(@Param("id") String id);

    List<MpSysCodeEnum> getAlldata(String code);

    List<MpSysCodeEnum> getSqlByCodes(@Param("param") List<JSONObject> codes);

    List<DeviceCloudTargetPO> getCloudTargetListByCodes(@Param("param") List<MpSysCodeEnum> filteredList);

    Boolean deleteCloudTarget(@Param("param") List<DeviceCloudTargetPO> cloudTargetList);

    Boolean deleteCloudTargetCalculate(@Param("param") List<DeviceCloudTargetCalculate> allCalculateList);

    Boolean deleteCloudTargetContainer(@Param("param") List<DeviceCloudTargetContainer> allContainerList);

    Boolean batchInsertCloudTarget(@Param("param") List<DeviceCloudTargetPO> list);

    Boolean batchInsertCloudTargetCalculate(@Param("param") List<DeviceCloudTargetCalculate> calculateList);

    Boolean batchInsertCloudTargetContainer(@Param("param") List<DeviceCloudTargetContainer> containerList);
}
