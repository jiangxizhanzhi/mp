package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.ImagesDeleteInstanceRequest;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.OriginIpsResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ResetCplcoudAllIpsData;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/22
 */
@Service
@Slf4j
public class RestCpcloudAllIpsService {
    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    @Autowired
    private ImagesConverter imagesConverter;

    @Autowired
    private DeviceTargetMapper deviceTargetMapper;

    @Autowired
    private CsceneInstanceService modelService;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    private ReentrantLock lock = new ReentrantLock();

    public ServiceResult<Boolean> clearAll() {
        if (!lock.tryLock()) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        } else {
            try {
                //首先要找到所有的需要清理的数据
                List<CsceneInstanceModel> allSceneInstances = getAllSceneInstances();
                // 查询所有启动的靶标实例
                List<DeviceTargetPO> allInstances = getAllInstances();
                Set<String> deletingIds = new HashSet<>();
                Set<String> deletedIds = new HashSet<>();
                if (CollectionUtils.isNotEmpty(allSceneInstances)) {
                    for (CsceneInstanceModel sceneInstance : allSceneInstances) {
                        modelService.deleteById(sceneInstance.getId(), true, new CsceneContextBO());
                        deletingIds.add(sceneInstance.getId());
                    }
                    // 轮询查看是否所有场景已删除结束
                    while (true) {
                        if (deletingIds.size() == deletedIds.size()) {
                            break;
                        }
                        for (CsceneInstanceModel sceneInstance : allSceneInstances) {
                            SceneHandler sceneHandler = new SceneHandler(sceneInstance.getScene(), null, null);
                            Integer event = sceneHandler.getScene().getEvent();
                            Integer eventStatus = sceneHandler.getScene().getEventStatus();
                            if (event == SceneModel.Event.DELETE && (eventStatus == EventStatus.OVER || eventStatus == EventStatus.ABNORMAL)) {
                                deletedIds.add(sceneInstance.getId());
                            }
                        }
                        Thread.sleep(3000);
                    }
                }
                if (CollectionUtils.isNotEmpty(allInstances)) {
                    for (DeviceTargetPO instance : allInstances) {
                        ImagesDeleteInstanceRequest request = new ImagesDeleteInstanceRequest();
                        request.setUuid(instance.getUuid());
                        request.setImageType(instance.getImageType());
                        imagesConverter.delete(request);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        return new ServiceResult<>(true);
    }

    public ServiceResult<Boolean> resetAllIps(ResetCplcoudAllIpsData request) {
        if (checkHasClear()) {
            //调云平台接口
            OriginIpsResult response = scenePluginCloudSceneService.resetCpcloudAllIps(request);
            log.info("reset cpcloud all ip response:{}", JsonCommon.stringify(response));
            if (response != null) {
                if (response.getSuccess()) {
                    return new ServiceResult<>(true).asResult();
                } else {
                    throw new MpRuntimeException(CsceneErrorCode.PARAMS_FIELD_ERROR.
                            format(convertMessages(CloudMessageConvertUtil.getMessage(response.getMessage()), request)));
                }
            }
        }

        return new ServiceResult<>(false).asResult();
    }

    public OriginIpsResult getOriginIps() {
        //调云平台接口
        OriginIpsResult response = scenePluginCloudSceneService.getCpcloudOriginIps();
        log.info("get cpcloud origin ips response:{}", JsonCommon.stringify(response));
        return response;
    }

    private Boolean checkHasClear() {
        if (lock.isLocked()) {
            // 正在清除，提示一下
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        }
        if (CollectionUtils.isNotEmpty(getAllInstances()) || CollectionUtils.isNotEmpty(getAllSceneInstances())) {
            // 还有没有清理的场景或实例
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_NEED_CLEAR);
        }
        return true;
    }

    private List<CsceneInstanceModel> getAllSceneInstances() {
        List<CsceneInstanceModel> csceneInstanceModels = csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getDeleted, 0));
        return csceneInstanceModels;
    }

    private List<DeviceTargetPO> getAllInstances() {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setVmStarted(true);
        List<DeviceTargetPO> allInstances = deviceTargetMapper.page(dto);
        return allInstances;
    }

    private String convertMessages(String message, ResetCplcoudAllIpsData resetCplcoudAllIpsData) {
        if (message != null) {
            message = message.replaceAll("\\{office_ip}",resetCplcoudAllIpsData.getOffice().getIp());
            if (message.contains("{cidr}")) {
                String ip = resetCplcoudAllIpsData.getController().getIp();
                String[] ips = ip.split("\\.");
                if (ips.length == 4) {
                    ips[3] = "0/24";
                }
                message = message.replaceAll("\\{cidr}",StringUtils.join(ips,"."));
            }
            message = message.replaceAll("\\{gateway}",resetCplcoudAllIpsData.getController().getGateway());
            if (CollectionUtils.isNotEmpty(resetCplcoudAllIpsData.getCompute())) {
                message = message.replaceAll("\\{compute_ip}",resetCplcoudAllIpsData.getCompute().get(0).getIp());
            }
            if (resetCplcoudAllIpsData.getController() != null && StringUtils.isNotEmpty(resetCplcoudAllIpsData.getController().getAllocationPools())) {
                message = message.replaceAll("\\{ip_list}",resetCplcoudAllIpsData.getController().getAllocationPools());
                message = message.replaceAll("\\{controller_ip}",resetCplcoudAllIpsData.getController().getIp());
            }

        }
        return message;
    }
}
