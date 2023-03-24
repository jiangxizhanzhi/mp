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
import cn.cyberpeace.mp.scene.util.ResourceClearHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.OriginIpsResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ResetCplcoudAllIpsData;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
    private ResourceClearHandler resourceClearHandler;

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
                var allSceneInstances = getAllSceneInstances();
                // 查询所有启动的靶标实例
                var allInstances = getAllInstances();
                if (CollectionUtils.isNotEmpty(allSceneInstances)) {
                    CountDownLatch countDownLatch = new CountDownLatch(allSceneInstances.size());
                    allSceneInstances.stream().forEach(sceneInstance -> {
                        resourceClearHandler.addCallback(sceneInstance.getId(), () -> {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
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
//                        imagesConverter.delete(request);
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
            // 正在清除，提示一下
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        }
        if (CollectionUtils.isNotEmpty(getAllInstances()) || CollectionUtils.isNotEmpty(getAllSceneInstances())) {
            // 还有没有清理的场景或实例
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_NEED_CLEAR);
        }
        return true;
    }

    public void resetAllIps(ResetCplcoudAllIpsData request) {
        if (checkHasClear()) {
            //调云平台接口
            var response = scenePluginCloudSceneService.resetCpcloudAllIps(request);
            log.info("reset cpcloud all ip response:{}", JsonCommon.stringify(response));
            resourceClearHandler.clearAll();
        }
    }

    public OriginIpsResult getOriginIps() {
        //调云平台接口
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
}
