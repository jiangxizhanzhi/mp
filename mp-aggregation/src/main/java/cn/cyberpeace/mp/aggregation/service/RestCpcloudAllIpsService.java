package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
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
import cn.cyberpeace.mp.scene.util.ResourceClearHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.OriginIpsResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ResetCplcoudAllIpsData;
import cn.cyberpeace.mp.sceneplugincloud.service.CloudCommonConfigService;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudmessageconvert.CloudMessageConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Service
@Slf4j
public class RestCpcloudAllIpsService {
    @Autowired
    private ScenePluginCloudSceneService scenePluginCloudSceneService;

    @Autowired
    private ResourceClearHandler resourceClearHandler;

    @Autowired
    private ImagesConverter imagesConverter;

    @Autowired
    private DeviceTargetMapper deviceTargetMapper;

    @Autowired
    private CsceneInstanceService modelService;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    @Resource
    private CloudCommonConfigService cloudCommonConfigService;

    private ReentrantLock lock = new ReentrantLock();

    public ServiceResult<Boolean> clearAll() {
        if (!lock.tryLock()) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        } else {
            try {
                
                var allSceneInstances = getAllSceneInstances();
                
                var allInstances = getAllInstances();
                if (CollectionUtils.isNotEmpty(allSceneInstances)) {
                    CountDownLatch countDownLatch = new CountDownLatch(allSceneInstances.size());
                    allSceneInstances.stream().forEach(sceneInstance -> {
                        resourceClearHandler.addCallback(sceneInstance.getId(), () -> {
                            countDownLatch.countDown();
                        });
                        modelService.deleteById(sceneInstance.getId(), true, new CsceneContextBO());
                    });
                    countDownLatch.await(allSceneInstances.size() * 3, TimeUnit.MINUTES);
                }
                if (CollectionUtils.isNotEmpty(allInstances)) {
                    allInstances.forEach(instance -> {
                        ImagesDeleteInstanceRequest request = new ImagesDeleteInstanceRequest();
                        request.setUuid(instance.getUuid());
                        request.setImageType(instance.getImageType());
                        imagesConverter.delete(request);
                    });
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        return new ServiceResult<>(true);
    }

    public Boolean checkHasClear() {
        if (lock.isLocked()) {
            
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        }
        if (CollectionUtils.isNotEmpty(getAllInstances()) || CollectionUtils.isNotEmpty(getAllSceneInstances())) {
            
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_NEED_CLEAR);
        }
        return true;
    }

    public ServiceResult<Boolean> resetAllIps(ResetCplcoudAllIpsData request) {
        if (checkHasClear()) {
            
            var response = scenePluginCloudSceneService.resetCpcloudAllIps(request);
            log.info("reset cpcloud all ip response:{}", JsonCommon.stringify(response));
            if (response != null) {
                if (response.getSuccess()) {
                    resourceClearHandler.clearAll();
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
        
        var response = scenePluginCloudSceneService.getCpcloudOriginIps();
        log.info("get cpcloud origin ips response:{}", JsonCommon.stringify(response));
        return response;
    }

    private List<CsceneInstanceModel> getAllSceneInstances() {
        var csceneInstanceModels = csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getDeleted, 0));
        return csceneInstanceModels;
    }

    private List<DeviceTargetPO> getAllInstances() {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setVmStarted(true);
        var allInstances = deviceTargetMapper.page(dto);
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
