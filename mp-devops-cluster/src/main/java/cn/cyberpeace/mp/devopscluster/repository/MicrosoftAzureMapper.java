package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.bo.MpSysCodeMenu;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.MpDevopsBoundaryNetworkEquipment;
import cn.cyberpeace.mp.devopscluster.po.*;
import cn.cyberpeace.mp.devopscluster.vo.EquipmentRequestVO;
import cn.cyberpeace.mp.devopscluster.vo.MpSysCodeMenuVO;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

/**
 * 云镜像管理
 */
@Mapper
@Repository
@ModelClass(DeviceCloudTargetPO.class)
public interface MicrosoftAzureMapper extends BaseModelMapper<DeviceCloudTargetPO> {

    List<DeviceCloudTargetPO> getCloudTargetList(@Param("param") JSONObject requestData);

    Integer getCloudTargetListTotal(@Param("param") JSONObject requestData);

    Integer deleteByIds(@Param("id") String id, @Param("param") List<String> ids);

    Integer deleteCalculatesByIds(@Param("id") String id, @Param("param") List<String> ids);

    Integer deleteContainersByIds(@Param("id") String id, @Param("param") List<String> ids);

    Boolean updateCloudTarget(@Param("id") String id, @Param("cloudServiceType") Integer cloudServiceType, @Param("param") JSONObject requestData);

    DeviceCloudTargetVO getCloudTargetCalculateById(@Param("id") String id);

    DeviceCloudTargetVO getCloudTargetContainerById(@Param("id") String id);

    DeviceCloudTargetVO getCloudTargetNetById(@Param("id") String id);

    LinkedList<MpSysCodeMenu> getAlldata(String code);

    List<MpSysCodeMenu> getSqlByCodes(@Param("param") List<MpSysCodeMenuVO> codes);

    List<DeviceCloudTargetVO> getCloudTargetListByCodes(@Param("cloudServiceType") Integer cloudServiceType,
                                                        @Param("param") List<MpSysCodeMenu> filteredList, @Param("offset") Integer offset,
                                                        @Param("pageSize") Integer pageSize, @Param("cloudType") String cloudType);

    Integer getCloudTargetListByCodesNum(@Param("cloudServiceType") Integer cloudServiceType, @Param("param") List<MpSysCodeMenu> filteredList, @Param("cloudType") String cloudType);


    Boolean deleteCloudTarget(@Param("param") List<DeviceCloudTargetPO> cloudTargetList, @Param("cloudType") String cloudType);

    Boolean deleteCloudTargetNet(@Param("param") List<DeviceCloudTargetNet> allNetList, @Param("cloudType") String cloudType);

    Boolean deleteCloudTargetCalculate(@Param("param") List<DeviceCloudTargetCalculate> allCalculateList, @Param("cloudType") String cloudType);

    Boolean deleteCloudTargetContainer(@Param("param") List<DeviceCloudTargetContainer> allContainerList, @Param("cloudType") String cloudType);

    Boolean batchInsertCloudTarget(@Param("param") List<DeviceCloudTargetPO> list);

    Boolean batchInsertCloudTargetNet(@Param("param") List<DeviceCloudTargetNet> netList);

    Boolean batchInsertCloudTargetCalculate(@Param("param") List<DeviceCloudTargetCalculate> calculateList);

    Boolean batchInsertCloudTargetContainer(@Param("param") List<DeviceCloudTargetContainer> containerList);

    List<MpDevopsBoundaryNetworkEquipment> getCommonTargets(@Param("param") EquipmentRequestVO request,
                                                            @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    Integer getCommonTargetNum(EquipmentRequestVO request, Integer offset, Integer pageSize);
}
