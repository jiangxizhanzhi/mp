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

    @Resource
    private CloudCommonConfigService cloudCommonConfigService;

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
            // 正在清除，提示一下
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_IS_CLEARING);
        }
        if (CollectionUtils.isNotEmpty(getAllInstances()) || CollectionUtils.isNotEmpty(getAllSceneInstances())) {
            // 还有没有清理的场景或实例
            throw new MpRuntimeException(CsceneErrorCode.SCENE_INSTANCE_NEED_CLEAR);
        }
        return true;
    }

    public ServiceResult<Boolean> resetAllIps(ResetCplcoudAllIpsData request) {
        String a = "Someone is currently modifying";
        String a1 = "execute modify web ip error";
        String a2 = "gateway {gateway} not in {cidr} IP range";
        String a3 = "gateway {gateway} cannot be the same as controller ip {gateway}";
        String a4 = "compute_ip {compute_ip} not in {cidr} IP range";
        String a5 = "compute_ip {controller_ip} cannot be the same as controller ip {controller_ip}";
        String a6 = "compute_ip {controller_ip} cannot be the same as controller ip {controller_ip}";
        String a7 = "gateway {gateway} cannot be the same as compute ip {gateway}";
        String a8 = "pools ip {ip} not in {cidr} IP range";
        String a9 = "The controller ip {controller_ip} cannot be within the address pool {ip_list} range";
        String a10 = "The compute ip {compute_ip} cannot be within the address pool {ip_list} range";
        String a11 = "The gateway {gateway} cannot be within the address pool {ip_list} range";
        String a12 = "office ip {office_ip} not in {cidr} IP range";
        String a13 = "office ip {office_ip} cannot be the same as controller ip {controller_ip}";
        String a14 = "office ip {office_ip} cannot be the same as compute ip {office_ip}";
        String a15 = "office ip {office_ip} cannot be the same as gateway ip {office_ip}";

        String b = convertMessages(CloudMessageConvertUtil.getMessage(a), request);
        String b1 = convertMessages(CloudMessageConvertUtil.getMessage(a1), request);
        String b2 = convertMessages(CloudMessageConvertUtil.getMessage(a2), request);
        String b3 = convertMessages(CloudMessageConvertUtil.getMessage(a3), request);
        String b4 = convertMessages(CloudMessageConvertUtil.getMessage(a4), request);
        String b5 = convertMessages(CloudMessageConvertUtil.getMessage(a5), request);
        String b6 = convertMessages(CloudMessageConvertUtil.getMessage(a6), request);
        String b7 = convertMessages(CloudMessageConvertUtil.getMessage(a7), request);
        String b8 = convertMessages(CloudMessageConvertUtil.getMessage(a8), request);
        String b9 = convertMessages(CloudMessageConvertUtil.getMessage(a9), request);
        String b10 = convertMessages(CloudMessageConvertUtil.getMessage(a10), request);
        String b711= convertMessages(CloudMessageConvertUtil.getMessage(a11), request);
        String b712= convertMessages(CloudMessageConvertUtil.getMessage(a12), request);
        String b713= convertMessages(CloudMessageConvertUtil.getMessage(a13), request);
        String b714= convertMessages(CloudMessageConvertUtil.getMessage(a14), request);
        String b715= convertMessages(CloudMessageConvertUtil.getMessage(a15), request);
        int a1111 = 1;
//        if (checkHasClear()) {
//            //调云平台接口
//            var response = scenePluginCloudSceneService.resetCpcloudAllIps(request);
//            log.info("reset cpcloud all ip response:{}", JsonCommon.stringify(response));
//            if (response != null) {
//                if (response.getSuccess()) {
//                    resourceClearHandler.clearAll();
//                    return new ServiceResult<>(true).asResult();
//                } else {
//                    throw new MpRuntimeException(CsceneErrorCode.PARAMS_FIELD_ERROR.
//                            format(convertMessages(CloudMessageConvertUtil.getMessage(response.getMessage()), request)));
//                }
//            }
//        }

        return new ServiceResult<>(false).asResult();
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
