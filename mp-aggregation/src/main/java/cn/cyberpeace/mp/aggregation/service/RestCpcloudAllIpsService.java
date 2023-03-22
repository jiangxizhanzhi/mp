package cn.cyberpeace.mp.aggregation.service;

import cn.cyberpeace.mp.aggregation.config.ResetCpcloudIpsUrl;
import cn.cyberpeace.mp.aggregation.utils.CpcloudHttpClient;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.util.SceneDeleteCallback;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.ImagesDeleteInstanceRequest;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.OriginIpsResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ResetCplcoudAllIpsData;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private SceneDeleteCallback sceneDeleteCallback;

    @Autowired
    private SceneMapper sceneMapper;

    @Autowired
    private ImagesConverter imagesConverter;

    @Autowired
    private DeviceTargetMapper deviceTargetMapper;

    @Autowired
    private CsceneInstanceService modelService;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;

    public ServiceResult<Boolean> clearAll() {
        if (sceneDeleteCallback.isClearing()) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        } else {
            //首先要找到所有的需要清理的数据
            var allSceneInstances = getAllSceneInstances();
            // 查询所有启动的靶标实例
            var allInstances = getAllInstances();

            if (CollectionUtils.isEmpty(allSceneInstances) && CollectionUtils.isEmpty(allInstances)) {
                //没有需要清理的场景和靶标实例
                //标记一下，已经执行过清理操作了
                sceneDeleteCallback.setStatus();
                //标记已经完成清理操作
                sceneDeleteCallback.removeClearingStatus();
                return new ServiceResult<>(true);
            } else {
                // 标识一下，正在清除,并标识已经执行过清除操作
                sceneDeleteCallback.setStatus();
                allSceneInstances.stream().forEach(sceneInstance -> {

                    sceneDeleteCallback.startClear();
                    sceneDeleteCallback.addCallback(sceneInstance.getId(), () -> {
                        sceneDeleteCallback.removeSceneOrInstanceId(sceneInstance.getId());
                        sceneDeleteCallback.finishClearJob();
                    });
                    modelService.deleteById(sceneInstance.getId(), true, new CsceneContextBO());

                });
                allInstances.forEach(instance -> {

                    ImagesDeleteInstanceRequest request = new ImagesDeleteInstanceRequest();
                    request.setUuid(instance.getUuid());
                    request.setImageType(instance.getImageType());

                    sceneDeleteCallback.startClear();
                    sceneDeleteCallback.addCallback(instance.getUuid(), () -> {
                        sceneDeleteCallback.removeSceneOrInstanceId(instance.getUuid());
                        sceneDeleteCallback.finishClearJob();
                    });
                    imagesConverter.delete(request);

                });

            }
        }

        return new ServiceResult<>(true);
    }

    public Boolean checkHasClear() {
        if (!sceneDeleteCallback.hasClear()) {
            // 还没有执行过清除操作，提示一下
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_NEED_CLEAR);
        }
        if (sceneDeleteCallback.isClearing()) {
            // 正在清除，提示一下
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        }
        if (CollectionUtils.isNotEmpty(getAllInstances()) || CollectionUtils.isNotEmpty(getAllSceneInstances())) {
            // 还有没有清理的场景或实例

        }
        return true;
    }

    public void resetAllIps(ResetCplcoudAllIpsData request) {
        if (checkHasClear()) {
            //调云平台接口
            int poolSize = 10;
            String server = "http://10.100.7.1:9999";
            Integer timeout = 6000;
            CpcloudHttpClient cpcloudHttpClient = new CpcloudHttpClient(server, timeout, poolSize);
            var response = cpcloudHttpClient.post(ResetCpcloudIpsUrl.RESET_CPCLOUD_ALL_IPS, request, null, OriginIpsResult.class);
            log.info("reset cpcloud all ip response:{}", JsonCommon.stringify(response));
            sceneDeleteCallback.clearAll();
        }
    }

    public OriginIpsResult getOriginIps() {
        //调云平台接口
        int poolSize = 10;
        String server = "http://10.100.7.1:9999";
        Integer timeout = 6000;
        CpcloudHttpClient cpcloudHttpClient = new CpcloudHttpClient(server, timeout, poolSize);
        var response = cpcloudHttpClient.get(ResetCpcloudIpsUrl.GET_CPCLOUD_ALL_ORIGIN_IPS, null, OriginIpsResult.class);
        log.info("get cpcloud origin ips response:{}", JsonCommon.stringify(response));
        return response;
    }

    private List<CsceneInstanceModel> getAllSceneInstances() {
        HumanCondition humanCondition = new HumanCondition();
        var csceneInstanceModels = csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getDeleted, 0));
        return csceneInstanceModels;
    }

    private List<DeviceTargetPO> getAllInstances() {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setVmStarted(true);
        var allInstances = deviceTargetMapper.page(dto);
        return allInstances;
    }
}
